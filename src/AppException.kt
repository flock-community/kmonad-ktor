package community.flock

import arrow.core.Either
import arrow.core.Left
import community.flock.common.Reader
import java.util.UUID

sealed class AppException(message: String) : Exception(message) {
    class NotFound(uuid: UUID) : AppException("$uuid Not found")
    object BadRequest : AppException("Bad Request")
}

fun exception(e: AppException): Either<AppException, Nothing> = Left(e)
inline fun <reified C> exceptionReader(e: AppException) = Reader<C, Either<AppException, Nothing>> { Left(e) }
