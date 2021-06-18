import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val jackson_version: String by project
val kotlin_version: String by project
val arrow_version: String by project
val kmongo_version: String by project
val swagger_generator_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.0"
    kotlin("kapt") version "1.5.0"
    id("com.sourcemuse.mongo") version "1.0.7"
}

group = "community.flock"
version = "0.0.1-SNAPSHOT"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")
    implementation("io.arrow-kt:arrow-mtl:$arrow_version")
    implementation("com.github.papsign:Ktor-OpenAPI-Generator:$swagger_generator_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    kapt("io.arrow-kt:arrow-meta:$arrow_version")
}

kotlin {
    sourceSets["main"].kotlin.srcDirs("src")
    sourceSets["test"].kotlin.srcDirs("test")
}

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks {
    withType<KotlinCompile> {
        configureEach {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        // Otherwise you'll get a "No main manifest attribute" error
        manifest { attributes["Main-Class"] = "community.flock.ApplicationKt" }

        // To add all of the dependencies otherwise a "NoClassDefFoundError" error
        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it) }
        })
    }

    test {
        dependsOn(startManagedMongoDb)
    }
}


mongo {
    setPort(12345)
    logging = "console"
}
