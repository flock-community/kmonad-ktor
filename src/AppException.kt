package community.flock

import arrow.core.Either
import arrow.core.Left
import community.flock.AppException.InternalServerError
import community.flock.common.Reader
import java.util.UUID

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NotFound(uuid: UUID) : AppException("$uuid Not found")
    object BadRequest : AppException("Bad Request")
    class InternalServerError(cause: Throwable?) : AppException("Internal Server Error", cause)
}

fun exception(e: AppException) = Left(e)
inline fun <reified C> exceptionReader(e: AppException) = Reader<C, Either<AppException, Nothing>> { Left(e) }

fun Throwable.internalize() = Left(InternalServerError(cause))
