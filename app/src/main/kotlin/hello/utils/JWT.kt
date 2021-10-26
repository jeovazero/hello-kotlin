package hello.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

val JWT_SECRET = "dumb-secret"

val JWT_HMAC_ALGORITHM = Algorithm.HMAC256(JWT_SECRET)

val EXPIRATION_IN_MILLIS = 5 * 60 * 1000

fun createJWTToken(username: String, id: String) = JWT.create()
    .withClaim("username", username)
    .withClaim("id", id)
    .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_IN_MILLIS))
    .sign(JWT_HMAC_ALGORITHM)