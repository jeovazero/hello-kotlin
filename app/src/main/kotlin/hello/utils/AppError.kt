package hello.utils

sealed class AppError

class EntityConflict : AppError()
class EntityNotFound : AppError()
class InvalidCredentials : AppError()