package hello.routes

import hello.utils.createJWTToken
import hello.domain.*
import hello.utils.*
import hello.models.NewUser
import hello.models.UserCredentials
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

suspend fun respondErr(call: ApplicationCall, err: AppError) {
    when (err) {
        is EntityConflict -> call.respondText("Conflict",
            status = HttpStatusCode.Conflict)
        is EntityNotFound -> call.respondText("Not found",
            status = HttpStatusCode.NotFound)
        is InvalidCredentials -> call.respondText("Invalid Credentials",
            status = HttpStatusCode.Unauthorized)
    }
}

fun Route.userRouting(userDomain: UserDomain) {
    route("/account") {
        post {
            try {
                val newUser = call.receive<NewUser>()
                val addUserResult = userDomain.addUser(newUser)

                when (addUserResult) {
                    is Ok -> call.respondText(addUserResult.ok,status=HttpStatusCode.Created)
                    is Err -> respondErr(call, addUserResult.err)
                }
            } catch (e: Exception) {
                println(e)
                call.respondText("INTERNAL SERVER ERROR",
                    status = HttpStatusCode.InternalServerError)
            }
        }
        authenticate("auth-jwt") {
            delete {
                val principal = call.principal<JWTPrincipal>()
                val id = principal!!.payload.getClaim("id").asString()
                userDomain.deleteUser(id)
                call.respond(status = HttpStatusCode.NoContent, message = "")
            }
        }
    }
    post("/login") {
        try {
            val userCredentials = call.receive<UserCredentials>()
            val userResult =
                userDomain.getUserByCredentials(userCredentials)
            when (userResult) {
                is Ok -> {
                    val token = createJWTToken(userResult.ok.name, userResult.ok.id)
                    call.respond(hashMapOf("token" to token))
                }
                is Err -> respondErr(call, userResult.err)
            }
        } catch (e: Exception) {
            println(e.printStackTrace())
            call.respondText("INTERNAL SERVER ERROR",
                status = HttpStatusCode.InternalServerError)
        }
    }

    authenticate("auth-jwt") {
        get("/authentication") {
            val principal = call.principal<JWTPrincipal>()
            val username =
                principal!!.payload.getClaim("username").asString()
            val expiresAt =
                principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}