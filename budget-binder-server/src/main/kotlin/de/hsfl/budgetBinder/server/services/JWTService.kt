package de.hsfl.budgetBinder.server.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import java.util.*

class JWTService {
    private val accessTokenSecret = System.getenv("JWT_ACCESS_SECRET") ?: throw Exception("No Access-Token Secret set")
    private val refreshTokenSecret = System.getenv("JWT_REFRESH_SECRET") ?: throw Exception("No Access-Token Secret set")
    private val issuer = System.getenv("JWT_ISSUER") ?: "http://0.0.0.0:8080/"
    private val audience = System.getenv("JWT_AUDIENCE") ?: "http://0.0.0.0:8080/"
    private val accessTokenValidationTime = 1000 * 60 * (System.getenv("JWT_ACCESS_MINUTES")?.toIntOrNull() ?: 15)
    private val refreshTokenValidationTime =
        1000 * 60 * 60 * 24 * (System.getenv("JWT_REFRESH_DAYS")?.toIntOrNull() ?: 7)

    private val accessTokenVerifier = JWT
        .require(Algorithm.HMAC256(accessTokenSecret))
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaimPresence("userid")
        .withClaimPresence("token_version")
        .build()

    private val refreshTokenVerifier = JWT
        .require(Algorithm.HMAC256(refreshTokenSecret))
        .withAudience(audience)
        .withIssuer(issuer)
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

    private fun createJWTToken(id: Int, tokenVersion: Int, expiresAt: Date, secret: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
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
            accessTokenSecret
        )
    }

    fun createRefreshToken(id: Int, tokenVersion: Int): String {
        return createJWTToken(
            id,
            tokenVersion,
            Date(System.currentTimeMillis() + refreshTokenValidationTime),
            refreshTokenSecret
        )
    }
}