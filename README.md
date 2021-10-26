#

A simple (and naive) RESTful API made with Ktor, jasync-sql and JWT.

| Route             | Method   | Description |
| ----------------- | -------- | ----------- |
| `/account`        | `POST`   | Create a new account | 
| `/account`        | `DELETE` | Delete an account (needs the JWT) |
| `/login`          | `POST`   | Return a JWT |
| `/authentication` | `GET`    | Return a message with the remaining time of the JWT |

## Running

1. `./gradlew dbStart` to run the postgres container 
2. `./gradlew flywayMigrate` to execute the migrations
3. `./gradlew run`