package community.flock.jedi.pipe

import arrow.core.Left
import arrow.core.Right
import community.flock.AppException
import community.flock.toReader
import java.util.UUID

object JediController {
    suspend fun getJediByUUID(uuidString: String?) = validate { UUID.fromString(uuidString) }
        .fold({ it.toReader() }, { JediService.getJediByUUID(it) })

    suspend fun getAllJedi() = JediService.getAllJedi()
}

private fun <R> validate(block: () -> R) = try {
    Right(block())
} catch (e: Exception) {
    Left(AppException.BadRequest)
}
