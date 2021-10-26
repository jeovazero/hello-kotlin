package hello.persistence.postgres

import com.github.jasync.sql.db.Connection
import hello.models.User
import hello.persistence.DBContext
import hello.persistence.UserPersistence
import java.util.*

class UserPersistencePG(val dbContext: DBContext<Connection>) :
    UserPersistence {
    override fun add(newUser: User): String {
        val (id, name, email, password) = newUser
        val future =
            dbContext.connection.sendPreparedStatement(
                "insert into hello.users values (?,?,?,?) returning user_id",
                listOf(id, name, email, password)
            )

        return future.get().rows[0].getAs<UUID>("user_id").toString()
    }

    override fun getByEmail(email: String): User? {
        val future =
            dbContext.connection.sendPreparedStatement(
                "select user_id,name,email,password from hello.users where email = ?",
                listOf(email)
            )

        val rows = future.get().rows

        return when (rows.size) {
            0 -> null
            else -> with(rows[0]) {
                User(
                    getAs<UUID>("user_id").toString(),
                    getAs("name"),
                    getAs("email"),
                    getAs("password"),
                )
            }
        }
    }

    override fun delete(id: String) {
        dbContext.connection.sendPreparedStatement(
            "delete from hello.users where user_id = ?",
            listOf(id)
        ).get()
    }
}
