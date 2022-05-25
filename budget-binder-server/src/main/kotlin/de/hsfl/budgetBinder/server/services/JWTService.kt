package de.hsfl.budgetBinder.server.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import de.hsfl.budgetBinder.server.config.Config
import java.util.Date

class JWTService(private val config: Config) {
    private val accessTokenValidationTime = 1000 * 60 * config.jwt.accessMinutes
    private val refreshTokenValidationTime = 1000 * 60 * 60 * 24 * config.jwt.refreshDays

    private val accessTokenVerifier = JWT
        .require(Algorithm.HMAC256(config.jwt.accessSecret))
        .withAudience(config.jwt.audience)
        .withIssuer(config.jwt.issuer)
        .withClaimPresence("userid")
        .withClaimPresence("token_version")
        .build()

    private val refreshTokenVerifier = JWT
        .require(Algorithm.HMAC256(config.jwt.refreshSecret))
        .withAudience(config.jwt.audience)
        .withIssuer(config.jwt.issuer)
        .withClaimPresence("userid")
        .withClaimPresence("token_version")
        .build()

    fun getAccessTokenVerifier(): JWTVerifier {
        return accessTokenVerifier
    }

    fun getRefreshTokenVerifier(): JWTVerifier {
        return refreshTokenVerifier
    }

    fun getRefreshTokenValidationTime(): Int {
        return refreshTokenValidationTime
    }

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

    fun createRefreshToken(id: Int, tokenVersion: Int): String {
        return createJWTToken(
            id,
            tokenVersion,
            Date(System.currentTimeMillis() + refreshTokenValidationTime),
            config.jwt.refreshSecret
        )
    }
}
