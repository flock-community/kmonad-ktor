package community.flock

import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import community.flock.droids.module as droidsModule
import community.flock.jedi.module as jediModule
import community.flock.sith.module as sithModule
import community.flock.wielders.module as forceWieldersModule

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    embeddedServer(
        Netty,
        applicationEngineEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load())
            val applicationPort = config.property("ktor.deployment.port")
                .getString()
                .toInt()

            connector {
                port = applicationPort
                host = config.property("ktor.deployment.host").getString()
            }

            module {
                commonModule()
                jediModule()
                sithModule()
                forceWieldersModule()
                droidsModule()
            }
        }
    ).start(wait = true)
}

fun Application.commonModule(){
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
