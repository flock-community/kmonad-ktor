package community.flock

import arrow.core.Either.Left
import arrow.mtl.Kleisli
import java.util.UUID

sealed class AppException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    class Conflict(uuid: String, cause: Throwable? = null) : AppException("$uuid already exists", cause)
    class NotFound(uuid: UUID, cause: Throwable? = null) : AppException("$uuid Not found", cause)
    class BadRequest(cause: Throwable? = null) : AppException("Bad Request", cause)
    class InternalServerError(cause: Throwable? = null) : AppException("Internal Server Error", cause)

    companion object {
        fun conflict(id: String) = { cause: Throwable? -> Conflict(id, cause) }
        fun notFound(uuid: UUID) = { cause: Throwable? -> NotFound(uuid, cause) }
    }
}

fun <D> AppException.toReader() = Kleisli { _: D -> Left(this) }
