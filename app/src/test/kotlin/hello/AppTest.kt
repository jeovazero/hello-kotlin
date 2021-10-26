import hello.createApp
import hello.utils.*
import hello.domain.UserDomain
import hello.models.NewUser
import hello.models.UserCredentials
import hello.persistence.postgres.DB_CONTEXT
import hello.persistence.postgres.UserPersistencePG
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.AfterClass
import org.junit.BeforeClass
import java.util.*
import kotlin.random.Random.Default.nextBytes
import kotlin.test.*

fun randomEmail(): String {
    val randomBytes = Base64.getEncoder().encodeToString(nextBytes(12))
    return "$randomBytes@yurnero.net"
}

@Serializable
data class Token(val token: String)

class ApplicationTest {
    val userDomain = UserDomain(UserPersistencePG(DB_CONTEXT))
    val app = createApp(userDomain)

    companion object {
        @JvmStatic
        @BeforeClass
        fun connectDB() {
            println("(╯°□°）╯︵ ┻━┻")
            DB_CONTEXT.connection.connect().get()
        }

        @JvmStatic
        @AfterClass
        fun disconnectDB() {
            DB_CONTEXT.connection.disconnect().get()
            println("┬─┬ノ( º _ ºノ)")
        }
    }

    @Test
    fun testRoot() {
        withTestApplication(app) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello friend!", response.content)
            }
        }
    }

    @Test
    fun `Register an user`() {
        withTestApplication(app) {
            val email = randomEmail()
            handleRequest(HttpMethod.Post, "/account") {
                setBody("""
                    {
                       "name": "Yurnero",
                        "email": "$email",
                        "password": "juggernaut"
                    }
                """)
                addHeader("Content-Type", "application/json")
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
            }
        }
    }

    @Test
    fun `Login of an user`() {
        withTestApplication(app) {
            val email = randomEmail()
            userDomain.addUser(NewUser("yurnero", email, "juggernaut"))
            handleRequest(HttpMethod.Post, "/login") {
                setBody("""
                    {
                        "email": "$email",
                        "password": "juggernaut"
                    }
                """)
                addHeader("Content-Type", "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)
                val responseToken = Json.decodeFromString<Token>(content)
                assertTrue(responseToken.token.length > 64)
                assertTrue(responseToken.token.count { it == '.' } == 2)
            }
        }
    }

    @Test
    fun `Authentication of an user`() {
        withTestApplication(app) {
            val email = randomEmail()
            val name = "yurnero"
            val result = userDomain.addUser(NewUser(name, email, "juggernaut"))
            val id = when (result) {
                is Ok -> result.ok
                else -> null
            }
            assertNotNull(id)
            val token = createJWTToken(name, id)
            handleRequest(HttpMethod.Get, "/authentication") {
                addHeader("Authorization", "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)
                assertContains(
                    content,
                    Regex("^Hello, $name! Token is expired at \\d+ ms.\$")
                )
            }
        }
    }

    @Test
    fun `Delete an user`() {
        withTestApplication(app) {
            val email = randomEmail()
            val name = "yurnero"
            val password = "juggernaut"
            val result = userDomain.addUser(NewUser(name, email, password))

            val id = when (result) {
                is Ok -> result.ok
                else -> null
            }
            assertNotNull(id)
            val token = createJWTToken(name, id)

            handleRequest(HttpMethod.Delete, "/account") {
                addHeader("Authorization", "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.NoContent, response.status())
                val resultUser = userDomain.getUserByCredentials(UserCredentials(name, password))
                assertTrue(resultUser is Err)
            }
        }
    }
}