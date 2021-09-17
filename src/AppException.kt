package community.flock

import arrow.core.Either.Left
import community.flock.common.IO
import community.flock.common.Reader.Factory.ask
import java.util.UUID

sealed class AppException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    class Conflict(uuid: UUID?, cause: Throwable? = null) : AppException("${uuid ?: "Entity"} already exists", cause)
    class NotFound(uuid: UUID, cause: Throwable? = null) : AppException("$uuid Not found", cause)
    class BadRequest(cause: Throwable? = null) : AppException("Bad Request", cause)
    class InternalServerError(cause: Throwable? = null) : AppException("Internal Server Error", cause)
}

fun <D> AppException.toReader() = ask<D>().map { IO {Left(this) } }
