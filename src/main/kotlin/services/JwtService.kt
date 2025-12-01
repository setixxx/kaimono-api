package setixx.software.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.repositories.UserRepository
import setixx.software.models.User
import java.util.*

class JwtService(
    private val application: Application,
    private val userRepository: UserRepository,
) {

    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    private val expiration = getConfigProperty("jwt.expiration")

    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    suspend fun createJwtToken(loginUserRequest: LoginUserRequest): String? {
        val foundUser: User? = userRepository.findByEmail(loginUserRequest.email)

        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", loginUserRequest.email)
            .withExpiresAt((Date(System.currentTimeMillis() + 3_600_000)))
            .sign(Algorithm.HMAC256(secret))
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
