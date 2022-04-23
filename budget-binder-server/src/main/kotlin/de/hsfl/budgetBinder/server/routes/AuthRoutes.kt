package de.hsfl.budgetBinder.server.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Post
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.date.*
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SameSite
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.*

fun createAccessToken(id: Int, tokenVersion: Int): String {
    val secret = System.getenv("JWT_ACCESS_SECRET")
    val minutes = System.getenv("JWT_ACCESS_MINUTES")?.toIntOrNull() ?: 15
    return createJWTToken(id, tokenVersion, secret, Date(System.currentTimeMillis() + 1000 * 60 * minutes))
}

fun createRefreshToken(id: Int, tokenVersion: Int, timestamp: Long): String {
    val secret = System.getenv("JWT_REFRESH_SECRET")
    return createJWTToken(id, tokenVersion, secret, Date(timestamp))
}

fun createJWTToken(id: Int, tokenVersion: Int, secret: String, expiresAt: Date): String {
    val issuer = System.getenv("JWT_ISSUER") ?: "http://0.0.0.0:8080/"
    val audience = System.getenv("JWT_AUDIENCE") ?: "http://0.0.0.0:8080/"

    return JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userid", id)
        .withClaim("token_version", tokenVersion)
        .withIssuedAt(Date(System.currentTimeMillis()))
        .withExpiresAt(expiresAt)
        .sign(Algorithm.HMAC256(secret))
}

fun createRefreshCookie(token: String, timestamp: Long): Cookie {
    val secure = System.getenv("DEV") != "True"
    val sameSiteValue = if (secure) SameSite.None.toString() else SameSite.Lax.toString()

    return Cookie(
        "jwt",
        token,
        expires = GMTDate(timestamp),
        path = "/refresh_token",
        httpOnly = true,
        secure = secure,
        extensions = hashMapOf(SAMESITE to sameSiteValue)
    )
}

fun Route.login() {
    authenticate("auth-form") {
        post("/login") {
            val user = call.principal<UserEntity>()!!

            val token = createAccessToken(user.id.value, user.tokenVersion)

            val days = System.getenv("JWT_REFRESH_DAYS")?.toIntOrNull() ?: 7
            val timestamp = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * days
            val refreshToken = createRefreshToken(user.id.value, user.tokenVersion, timestamp)

            call.response.cookies.append(createRefreshCookie(refreshToken, timestamp))
            call.respond(APIResponse(hashMapOf("token" to token)))
        }
    }
}

fun Route.refreshCookie() {
    get("/refresh_token") {
        val tokenToCheck = call.request.cookies["jwt"]

        if (tokenToCheck == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                APIResponse<Post>(null, "No Refresh Cookie", false)
            )
            return@get
        }
        val secret = System.getenv("JWT_REFRESH_SECRET")
        val issuer = System.getenv("JWT_ISSUER") ?: "http://0.0.0.0:8080/"
        val audience = System.getenv("JWT_AUDIENCE") ?: "http://0.0.0.0:8080/"

        val token = JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaimPresence("userid")
            .withClaimPresence("token_version")
            .build().verify(tokenToCheck)

        val id = token.getClaim("userid").asInt()
        val tokenVersion = token.getClaim("token_version").asInt()
        val userService: UserService by closestDI().instance()
        val user = userService.findUserByID(id)

        if (user?.tokenVersion != tokenVersion) {
            call.respond(
                HttpStatusCode.Unauthorized,
                APIResponse<String>(null, "Token Version is different", false)
            )
            return@get
        }

        val accessToken = createAccessToken(id, tokenVersion)

        val days = System.getenv("JWT_REFRESH_DAYS")?.toIntOrNull() ?: 7
        val timestamp = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * days

        val refreshToken = createRefreshToken(id, tokenVersion, timestamp)

        call.response.cookies.append(createRefreshCookie(refreshToken, timestamp))
        call.respond(APIResponse(hashMapOf("token" to accessToken)))
    }
}

// install all previous Routes
fun Application.authRoutes() {
    routing {
        login()
        refreshCookie()
    }
}
