package de.hsfl.budgetBinder.server.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import de.hsfl.budgetBinder.server.config.Config
import io.ktor.http.*
import io.ktor.util.date.*
import io.netty.handler.codec.http.cookie.CookieHeaderNames
import java.util.Date

class JWTService(private val config: Config) {
    private val accessTokenValidationTime = 1000 * 60 * config.jwt.accessMinutes
    private val refreshTokenValidationTime = 1000 * 60 * 60 * 24 * config.jwt.refreshDays

    val accessTokenVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(config.jwt.accessSecret))
        .withAudience(config.jwt.audience)
        .withIssuer(config.jwt.issuer)
        .withClaimPresence("userid")
        .withClaimPresence("token_version")
        .build()

    private val refreshTokenVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(config.jwt.refreshSecret))
        .withAudience(config.jwt.audience)
        .withIssuer(config.jwt.issuer)
        .withClaimPresence("userid")
        .withClaimPresence("token_version")
        .build()

    fun getRealm(): String {
        return config.jwt.realm
    }

    private fun createJWTToken(id: Int, tokenVersion: Int, expiresAt: Date, secret: String): String {
        return JWT.create()
            .withAudience(config.jwt.audience)
            .withIssuer(config.jwt.issuer)
            .withClaim("userid", id)
            .withClaim("token_version", tokenVersion)
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC256(secret))
    }

    fun createAccessToken(id: Int, tokenVersion: Int): String {
        return createJWTToken(
            id,
            tokenVersion,
            Date(System.currentTimeMillis() + accessTokenValidationTime),
            config.jwt.accessSecret
        )
    }

    private fun createRefreshToken(id: Int, tokenVersion: Int): String {
        return createJWTToken(
            id,
            tokenVersion,
            Date(System.currentTimeMillis() + refreshTokenValidationTime),
            config.jwt.refreshSecret
        )
    }

    fun createRefreshCookie(id: Int, tokenVersion: Int, isHttps: Boolean): Cookie {
        return Cookie(
            "jwt",
            createRefreshToken(id, tokenVersion),
            expires = GMTDate(System.currentTimeMillis() + refreshTokenValidationTime),
            path = "/refresh_token",
            httpOnly = true,
            secure = isHttps,
            extensions = hashMapOf(CookieHeaderNames.SAMESITE to CookieHeaderNames.SameSite.Strict.toString())
        )
    }

    fun verifyRefreshToken(tokenToCheck: String): DecodedJWT? {
        return try {
            refreshTokenVerifier.verify(tokenToCheck)
        } catch (_: JWTVerificationException) {
            null
        }
    }
}
