package community.flock

import com.fasterxml.jackson.databind.SerializationFeature
import community.flock.jedi.pipe.LiveJediController
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    val httpClient = HttpClient(Apache) {}

    val client = KMongo.createClient().coroutine

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

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/db") {
            call.respond(LiveJediController.getAllJedi())
        }

        get("/db/{uuid}") {
            call.respond(LiveJediController.getJediByUUID(call.parameters["uuid"]))
        }
    }
}
