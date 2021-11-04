package hello.persistence.postgres

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import hello.persistence.DBContext
import java.util.concurrent.TimeUnit

// postgresql://[user[:password]@][netloc][:port][/dbname]
// Below, a naive implementation

val postgresPrefix = "postgresql://"

typealias CharPredicate = (Char) -> Boolean

private fun String.breaker(stop: CharPredicate): Pair<String,String>? {
    var c: Char
    for (index in indices) {
        c = this[index]
        if (stop(c)) {
            return Pair(
                this.substring(0,index - 1),
                this.substring(index+1)
            )
        }
    }
    return null
}

private fun String.spliter(chars: CharSequence): List<String>? {
    var start = this
    val list = mutableListOf<String>()
    for(c in chars) {
        val ans = start.breaker{ it == c }
        ans ?: return null
        val (part,rest) = ans
        if (part.trim().isEmpty()) return null
        list.add(part)
        start = rest
    }
    return list
}

data class PostgresConfig(
    val user: String,
    val pass: String,
    val netloc: String,
    val port: Int,
    val db: String
    )

// Say no to Regex
fun parsePostgresUri(uri: String): PostgresConfig? {
    if (!uri.startsWith(postgresPrefix)) return null
    val uriRem = uri.removePrefix(postgresPrefix)
    val result = uriRem.spliter(":@:/")
    result ?: return null
    val (user, pass, netloc, portStr, db) = result
    val port = portStr.toIntOrNull()
    port ?: return null
    return PostgresConfig(user,pass,netloc,port,db)
}

fun getPostgresConfig(): PostgresConfig {
    val uri = System.getenv("DATABASE_URI") ?: ""
    return parsePostgresUri(uri)
        ?: PostgresConfig(
            "elefante",
            "elefantinho",
            "localhost",
            5432,
            "elefante"
        )
}

fun makeConnection(): Connection {
    val config = getPostgresConfig()
    return PostgreSQLConnectionBuilder.createConnectionPool {
        username = config.user
        host = config.netloc
        port = config.port
        password = config.pass
        database = config.db
        maxActiveConnections = 100
        maxIdleTime = TimeUnit.MINUTES.toMillis(15)
        maxPendingQueries = 10_000
        connectionValidationInterval = TimeUnit.SECONDS.toMillis(30)
    }
}

val DB_CONTEXT = DBContext(makeConnection())