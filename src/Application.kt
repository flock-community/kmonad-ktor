package community.flock

import arrow.core.Either
import arrow.core.getOrHandle
import com.fasterxml.jackson.databind.SerializationFeature
import community.flock.AppException.BadRequest
import community.flock.AppException.InternalServerError
import community.flock.AppException.NotFound
import community.flock.common.Env.mongoDbHost
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
import kotlinx.coroutines.flow.toList

typealias Ctx = PipelineContext<Unit, ApplicationCall>

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    val httpClient = HttpClient(Apache) {}
    val jediRepository = LiveJediRepository.instance(mongoDbHost())

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(StatusPages) {
        exception<AppException> {
            val code = when (it) {
                is BadRequest -> HttpStatusCode.BadRequest
                is NotFound -> HttpStatusCode.NotFound
                is InternalServerError -> HttpStatusCode.InternalServerError
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
            runWith(jediRepository) { JediController.getAllJedi() }.toList()
                .let { call.respond(it) }
        }

        get("/db/{uuid}") {
            runWith(jediRepository) { JediController.getJediByUUID(call.parameters["uuid"]) }
                .let { call.respond(it) }
        }
    }
}

suspend fun <D : Repository, R : Any> Ctx.runWith(repo: D, block: suspend () -> Reader<D, Either<AppException, R>>): R =
    block().run(repo).getOrHandle { throw it }
