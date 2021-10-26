package hello.domain

import hello.models.NewUser
import hello.models.User
import hello.models.UserCredentials
import hello.persistence.UserPersistence
import hello.utils.*
import java.util.*

typealias UserId = String

class UserDomain(private val userPersistence: UserPersistence) {
    fun getUserByCredentials(userCredentials: UserCredentials): Result<User, AppError> {
        val userResult = userPersistence.getByEmail(userCredentials.email)
        if (userResult === null) {
            return Err(InvalidCredentials())
        }
        val plainPassword = userCredentials.password
        val hashedPassword = userResult.hashedPassword
        if (verifyHashedPassword(plainPassword, hashedPassword)) {
            return Ok(userResult)
        }
        return Err(InvalidCredentials())
    }

    fun addUser(newUser: NewUser): Result<UserId, AppError> {
        val userResult = userPersistence.getByEmail(newUser.email)
        if (userResult !== null) {
            return Err(EntityConflict())
        }
        val (name, email, password) = newUser

        val id = UUID.randomUUID()
        val hash = createHashedPassword(password)
        val user = User(id.toString(), name, email, hash)

        return Ok(userPersistence.add(user))
    }

    fun deleteUser(id: String): Result<Unit,AppError> {
        userPersistence.delete(id)

        return Ok(Unit)
    }
}