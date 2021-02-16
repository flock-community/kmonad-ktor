package community.flock

import arrow.core.Either
import com.fasterxml.jackson.databind.SerializationFeature
import community.flock.common.Reader
import community.flock.common.Repository
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

typealias Ctx = PipelineContext<Unit, ApplicationCall>
typealias Result<D, R> = Reader<D, Either<AppException, R>>

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }

}

suspend inline fun <D : Repository, reified R : Any> Ctx.respond(repo: D, block: () -> Result<D, R>): Unit =
    block().run(repo).fold({ handle(it) }, { call.respond(it) })

suspend inline fun <D : Repository, R : Any> Ctx.respondFlow(repo: D, block: () -> Result<D, Flow<R>>): Unit =
    block().run(repo).fold({ handle(it) }, { call.respond(it.toList()) })

suspend fun Ctx.handle(e: AppException) = call.respond(
    when (e) {
        is AppException.BadRequest -> BadRequest
        is AppException.NotFound -> NotFound
        is AppException.InternalServerError -> InternalServerError
    },
    e.message ?: ""
)
