/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.8.3/userguide/building_java_projects.html
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by extra
val ktor_version: String by extra
val logback_version: String by extra
val jasync_version: String by extra
val bcrypt_version: String by extra

group = "hello"
version = "1.0"

plugins {
    application
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    id("org.flywaydb.flyway") version "8.0.0-beta2"
}

flyway {
    url = "jdbc:postgresql://localhost:5432/elefante"
    user = "elefante"
    password = "elefantinho"
}

repositories {
    mavenCentral()
    // jcenter()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")

    implementation("com.github.jasync-sql:jasync-postgresql:$jasync_version")
    implementation("at.favre.lib:bcrypt:$bcrypt_version")

    // jwt
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")

    runtimeOnly("org.postgresql:postgresql:42.2.24")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")

    // Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Align versions of all Kotlin components
    implementation(kotlin("bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
}

application {
    // Define the main class for the application.
    mainClass.set("hello.AppKt")

    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestJava {
        targetCompatibility = "1.8"
    }

    register<Exec>("dbStart") {
        description = "Start the little 'Elefante'"
        workingDir("../")
        commandLine("./scripts/postgres.sh","start")
    }
    register<Exec>("dbStop") {
        description = "Stop the little 'Elefante'"
        workingDir("../")
        commandLine("./scripts/postgres.sh","stop")
    }
}