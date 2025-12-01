package setixx.software.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.repositories.jwt.JwtRepository
import setixx.software.data.repositories.user.UserRepository
import setixx.software.data.tables.Tokens.deviceInfo
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

    suspend fun createAccessToken(loginUserRequest: LoginUserRequest): String? {
        val foundUser: User = userService.login(loginUserRequest)

        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", loginUserRequest.email)
            .withExpiresAt((Date(System.currentTimeMillis() + accessExpiration)))
            .sign(Algorithm.HMAC256(secret))
    }

    suspend fun createRefreshToken(
        loginUserRequest: LoginUserRequest,
        deviceInfo: String
    ): String {
        val foundUser: User = userService.login(loginUserRequest)
        val now = Instant.now()
        val expiresAt = Instant.ofEpochMilli(System.currentTimeMillis() + refreshExpiration)

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", loginUserRequest.email)
            .withExpiresAt(Date.from(expiresAt))
            .sign(Algorithm.HMAC256(secret))

        jwtRepository.saveToken(
            userId = foundUser.id,
            token = token,
            expiresAt = expiresAt,
            createdAt = now,
            deviceInfo = deviceInfo
        )
        return token
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
