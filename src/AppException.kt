package community.flock

import arrow.core.Left
import community.flock.common.Reader.Factory.ask
import java.util.UUID

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NotFound(uuid: UUID) : AppException("$uuid Not found")
    object BadRequest : AppException("Bad Request")
    class InternalServerError(cause: Throwable?) : AppException("Internal Server Error", cause)
}

fun <C> AppException.toReader() = ask<C>().map { Left(this) }
