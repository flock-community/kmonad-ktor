package community.flock

import arrow.core.Either
import arrow.core.getOrHandle
import com.fasterxml.jackson.databind.SerializationFeature
import community.flock.common.Reader
import community.flock.common.Repository
import community.flock.jedi.pipe.JediController
import community.flock.jedi.pipe.LiveJediRepository
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.pipeline.PipelineContext

typealias Ctx = PipelineContext<Unit, ApplicationCall>

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
            runWith(LiveJediRepository) { JediController.getJediByUUID(call.parameters["uuid"]) }
        }
    }
}

suspend fun <D : Repository> Ctx.runWith(repo: D, block: suspend () -> Reader<D, Either<AppException, Any>>) =
    call.respond(block().run(repo).getOrHandle { throw it })
