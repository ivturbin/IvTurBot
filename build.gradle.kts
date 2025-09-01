plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "9.0" // JOOQ Codegen плагин
}

group = "dev.turbin"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}


repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    val jooqVersion = "3.19.19"

    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.springframework.boot:spring-boot-starter-jooq") {
        exclude(group = "org.jooq", module = "jooq") // чтобы не подтягивалась старая версия
    }
    jooqGenerator("org.jooq:jooq-codegen:$jooqVersion")
    jooqGenerator("org.postgresql:postgresql")
    implementation("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jooq {
    version.set("3.19.19")
    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/botdb"
                    user = System.getenv("JDBC_USER") ?: "postgres"
                    password = System.getenv("JDBC_PASSWORD") ?: "postgres"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                        isPojos = true
                        isDaos = false
                    }
                    target.apply {
                        packageName = "dev.turbin.ivturbot.jooq"
                        directory = "build/generated-jooq"
                    }
                }
            }
        }
    }
}

sourceSets["main"].java {
    srcDir("build/generated-jooq")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("bot.jar")
}


