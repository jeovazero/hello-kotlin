package hello.persistence

import hello.models.User

interface UserPersistence {
    fun add(newUser: User): String
    fun getByEmail(email: String): User?
    fun delete(id: String)
}