package setixx.software.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import setixx.software.data.dto.LoginResponse
import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.repositories.JwtRepository
import setixx.software.data.repositories.UserRepository
import setixx.software.data.tables.Tokens.deviceInfo
import setixx.software.models.Token
import setixx.software.models.User
import java.time.Instant
import java.util.*

class JwtService(
    private val application: Application,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val jwtRepository: JwtRepository
) {

    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    private val refreshExpiration = getConfigProperty("jwt.refresh-expiration").toLong()
    private val accessExpiration = getConfigProperty("jwt.access-expiration").toLong()

    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    private fun createAccessToken(email: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", email)
            .withExpiresAt((Date(System.currentTimeMillis() + accessExpiration)))
            .sign(Algorithm.HMAC256(secret))
    }

    private suspend fun createRefreshTokenAndSave(
        userId: Long,
        email: String,
        deviceInfo: String
    ): String {
        val now = Instant.now()
        val expiresAt = Instant.ofEpochMilli(System.currentTimeMillis() + refreshExpiration)

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", email)
            .withExpiresAt(Date.from(expiresAt))
            .sign(Algorithm.HMAC256(secret))

        jwtRepository.saveToken(
            userId = userId,
            token = token,
            expiresAt = expiresAt,
            createdAt = now,
            deviceInfo = deviceInfo
        )
        return token
    }

    suspend fun createAccessToken(loginUserRequest: LoginUserRequest): String? {
        val foundUser: User = userService.login(loginUserRequest)
        return createAccessToken(foundUser.email)
    }

    suspend fun createRefreshToken(
        loginUserRequest: LoginUserRequest,
        deviceInfo: String
    ): String {
        val foundUser: User = userService.login(loginUserRequest)
        val foundToken: Token? = jwtRepository.findTokenByUser(foundUser.id)
        foundToken?.let {
            jwtRepository.deleteTokenByUser(foundUser.id)
        }
        return createRefreshTokenAndSave(foundUser.id, foundUser.email, deviceInfo)
    }

    suspend fun reissueTokens(
        refreshToken: String,
        deviceInfo: String
    ): LoginResponse {
        val jwtVerifierRefresh = JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

        val decodedJWT = try {
            jwtVerifierRefresh.verify(refreshToken)
        } catch (e: Exception) {
            application.log.error("Refresh token verification failed", e)
            throw IllegalArgumentException("Invalid refresh token")
        }

        val email = decodedJWT.getClaim("email").asString()
            ?: throw IllegalArgumentException("Invalid refresh token payload")

        val foundUser: User = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User not found for refresh token")

        val storedToken = jwtRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Refresh token is not registered in the database")

        val now = Instant.now()
        if (storedToken.expiresAt.isBefore(now)) {
            jwtRepository.deleteToken(refreshToken)
            throw IllegalArgumentException("Refresh token expired")
        }

        jwtRepository.deleteToken(refreshToken)

        val newAccessToken = createAccessToken(foundUser.email)
        val newRefreshToken = createRefreshTokenAndSave(foundUser.id, foundUser.email, deviceInfo)

        return LoginResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    suspend fun logout(refreshToken: String) {
        jwtRepository.deleteToken(refreshToken)
    }

    suspend fun customValidator(
        credential: JWTCredential,
    ): JWTPrincipal? {
        val email: String? = extractEmail(credential)
        val foundUser: User? = email?.let { userRepository.findByEmail(it) }

        return foundUser?.let {
            if (audienceMatches(credential))
                JWTPrincipal(credential.payload)
            else
                null
        }
    }

    private fun audienceMatches(
        credential: JWTCredential,
    ): Boolean =
        credential.payload.audience.contains(audience)

    private fun getConfigProperty(path: String) =
        application.environment.config.property(path).getString()

    private fun extractEmail(credential: JWTCredential): String? =
        credential.payload.getClaim("email").asString()
}
