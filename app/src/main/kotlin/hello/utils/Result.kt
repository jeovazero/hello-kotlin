package hello.utils

sealed class Result<out T, out E>

data class Ok<T>(val ok: T) : Result<T, Nothing>()
data class Err<E>(val err: E) : Result<Nothing, E>()