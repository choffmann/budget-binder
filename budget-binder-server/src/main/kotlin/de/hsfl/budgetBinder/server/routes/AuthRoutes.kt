package de.hsfl.budgetBinder.server.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Post
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.date.*
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SameSite
import java.util.*

fun createAccessToken(id: Int): String {
    val secret = System.getenv("JWT_ACCESS_SECRET")
    return createJWTToken(id, secret, Date(System.currentTimeMillis() + 1000 * 60 * 15))
}

fun createRefreshToken(id: Int): String {
    val secret = System.getenv("JWT_REFRESH_SECRET")
    return createJWTToken(id, secret, Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
}

fun createJWTToken(id: Int, secret: String, expiresAt: Date): String {
    val issuer = System.getenv("JWT_ISSUER") ?: "http://0.0.0.0:8080/"
    val audience = System.getenv("JWT_AUDIENCE") ?: "http://0.0.0.0:8080/"

    return JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userid", id)
        .withIssuedAt(Date(System.currentTimeMillis()))
        .withExpiresAt(expiresAt)
        .sign(Algorithm.HMAC256(secret))
}

fun createRefreshCookie(token: String): Cookie {
    val secure = System.getenv("DEV") != "True"
    val sameSiteValue = if (secure) SameSite.None.toString() else SameSite.Lax.toString()

    return Cookie(
        "jwt",
        token,
        expires = GMTDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7),
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

            val token = createAccessToken(user.id.value)
            val refreshToken = createRefreshToken(user.id.value)

            call.response.cookies.append(createRefreshCookie(refreshToken))
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
            .build().verify(tokenToCheck)

        val id = token.getClaim("userid").asInt()

        val accessToken = createAccessToken(id)
        val refreshToken = createRefreshToken(id)

        call.response.cookies.append(createRefreshCookie(refreshToken))
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
