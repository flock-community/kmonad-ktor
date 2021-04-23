package community.flock.sith

import community.flock.AppException
import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.LiveLogger
import community.flock.common.define.Logger
import community.flock.sith.define.Context
import community.flock.sith.pipe.LiveRepository.Companion.liveRepository
import community.flock.sith.pipe.bindDelete
import community.flock.sith.pipe.bindGet
import community.flock.sith.pipe.bindPost
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.flow.toList

typealias Ctx = PipelineContext<Unit, ApplicationCall>

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val host = getProp("ktor.db.host", "localhost")

    moduleWith(object : Context {
        override val sithRepository = DataBase.instance(host).liveRepository()
        override val logger: Logger = LiveLogger
    })

}

fun Application.moduleWith(context: Context) {
    routing {
        get("/sith") {
            handle { call.respond(context.bindGet().toList()) }
        }

        get("/sith/{uuid}") {
            handle { call.respond(context.bindGet(call.parameters["uuid"])) }
        }

        post("/sith") {
            handle { call.respond(context.bindPost(call.receive())) }
        }

        delete("/sith/{uuid}") {
            handle { call.respond(context.bindDelete(call.parameters["uuid"])) }
        }
    }
}

suspend fun <R> Ctx.handle(block: suspend () -> R) = try {
    block()
} catch (e: AppException) {
    call.respond(
        when (e) {
            is AppException.BadRequest -> BadRequest
            is AppException.NotFound -> NotFound
            is AppException.InternalServerError -> InternalServerError
        },
        e.message ?: ""
    )
}
