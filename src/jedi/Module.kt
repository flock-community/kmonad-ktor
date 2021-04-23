package community.flock.jedi

import community.flock.AppException
import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.LiveLogger
import community.flock.jedi.define.Context
import community.flock.jedi.pipe.LiveRepository.Companion.liveRepository
import community.flock.jedi.pipe.bindDelete
import community.flock.jedi.pipe.bindGet
import community.flock.jedi.pipe.bindPost
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

    moduleWithDependencies(object : Context {
        override val logger = LiveLogger
        override val jediRepository = DataBase.instance(host).liveRepository()
    })

}

fun Application.moduleWithDependencies(context: Context) {

    routing {
        get("/jedi") {
            bindGet()
                .run(context)
                .fold({ handle(it) }, { call.respond(it.toList()) })
        }

        get("/jedi/{uuid}") {
            bindGet(call.parameters["uuid"])
                .run(context)
                .fold({ handle(it) }, { call.respond(it) })
        }

        post("/jedi") {
            bindPost(call.receive())
                .run(context)
                .fold({ handle(it) }, { call.respond(it) })
        }

        delete("/jedi/{uuid}") {
            bindDelete(call.parameters["uuid"])
                .run(context)
                .fold({ handle(it) }, { call.respond(it) })
        }
    }

}

suspend fun Ctx.handle(e: AppException) = call.respond(
    when (e) {
        is AppException.BadRequest -> BadRequest
        is AppException.NotFound -> NotFound
        is AppException.InternalServerError -> InternalServerError
    },
    e.message ?: ""
)
