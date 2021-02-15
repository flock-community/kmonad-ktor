package community.flock.jedi.pipe

import community.flock.AppException
import community.flock.exceptionReader
import kotlinx.coroutines.flow.toList
import java.util.UUID

object JediController {
    suspend fun getJediByUUID(uuidString: String?) = runCatching { UUID.fromString(uuidString) }
        .fold(
            { JediService.getJediByUUID(it) },
            { exceptionReader(AppException.BadRequest) }
        )

    suspend fun getAllJedi() = JediService.getAllJedi().toList()

}
