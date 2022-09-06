import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val jackson_version: String by project
val kotlin_version: String by project
val kmongo_version: String by project
val swagger_generator_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10"
    id("com.sourcemuse.mongo") version "2.0.0"
}

group = "community.flock"
version = "0.0.1-SNAPSHOT"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("community.flock:kmonad-core:0.0.1-SNAPSHOT")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
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
                jvmTarget = "17"
                freeCompilerArgs = listOf("-Xcontext-receivers", "-Xskip-prerelease-check" , "-opt-in=kotlin.RequiresOptIn")
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
