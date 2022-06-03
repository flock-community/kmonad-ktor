package community.flock.common

import community.flock.kmonad.core.AppException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<Unit, ApplicationCall>.handleErrors(e: Throwable) = when (e) {
    is AppException -> handleErrors(e)
    else -> call.respond(HttpStatusCode.InternalServerError)
}


suspend fun PipelineContext<Unit, ApplicationCall>.handleErrors(e: AppException) = when (e) {
    is AppException.BadRequest -> HttpStatusCode.BadRequest
    is AppException.Conflict -> HttpStatusCode.Conflict
    is AppException.NotFound -> HttpStatusCode.NotFound
    is AppException.InternalServerError -> HttpStatusCode.InternalServerError
}.let { call.respond(it) }
