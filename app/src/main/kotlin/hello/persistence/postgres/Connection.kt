package hello.persistence.postgres

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import hello.persistence.DBContext
import java.util.concurrent.TimeUnit

fun makeConnection(): Connection = PostgreSQLConnectionBuilder.createConnectionPool {
    username = "elefante"
    host = "localhost"
    port = 5432
    password = "elefantinho"
    database = "elefante"
    maxActiveConnections = 100
    maxIdleTime = TimeUnit.MINUTES.toMillis(15)
    maxPendingQueries = 10_000
    connectionValidationInterval = TimeUnit.SECONDS.toMillis(30)
}

val DB_CONTEXT = DBContext(makeConnection())