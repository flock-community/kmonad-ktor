package community.flock

import com.fasterxml.jackson.databind.SerializationFeature
import community.flock.jedi.pipe.JediController
import community.flock.jedi.pipe.LiveJediRepository
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    val httpClient = HttpClient(Apache) {}

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(StatusPages) {
        exception<AppException> {
            val code = when (it) {
                is AppException.BadRequest -> HttpStatusCode.BadRequest
                is AppException.NotFound -> HttpStatusCode.NotFound
            }
            call.respond(code, it.message ?: "")
        }
    }

    val dbHost = environment.config.propertyOrNull("ktor.db.host")?.getString() ?: "localhost"
    LiveJediRepository.host = dbHost

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/db") {
            call.respond(JediController.getAllJedi())
        }

        get("/db/{uuid}") {
            call.respond(JediController.getJediByUUID(call.parameters["uuid"]))
        }
    }
}
