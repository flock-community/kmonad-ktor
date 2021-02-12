package community.flock

import arrow.core.Left
import java.util.*

sealed class AppException(message: String) : Exception(message) {
    class NotFound(uuid: UUID) : AppException("$uuid Not found")
    object BadRequest : AppException("Bad Request")
}

fun exception(e: AppException) = Left(e)
