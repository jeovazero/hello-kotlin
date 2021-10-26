package hello.models

import kotlinx.serialization.Serializable

@JvmInline
value class HashedPassword(val password: String)

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val hashedPassword: String
)

@Serializable
data class NewUser(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserCredentials(
    val email: String,
    val password: String,
)