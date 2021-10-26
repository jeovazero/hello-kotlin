package hello.utils

import at.favre.lib.crypto.bcrypt.BCrypt

fun createHashedPassword(password: String) =
    BCrypt.withDefaults().hash(12, password.toByteArray())
        .toString(Charsets.UTF_8)

fun verifyHashedPassword(password: String, hash: String) =
    BCrypt.verifyer().verify(password.toCharArray(), hash).verified
