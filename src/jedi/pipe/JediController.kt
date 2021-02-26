package community.flock.jedi.pipe

import arrow.core.Either
import community.flock.AppException
import community.flock.common.Reader
import community.flock.exceptionReader
import community.flock.jedi.data.Jedi
import java.util.UUID

object JediController {
    suspend fun getJediByUUID(uuidString: String?): Reader<JediRepository, Either<AppException, Jedi>> = runCatching { UUID.fromString(uuidString) }
        .fold(
            { JediService.getJediByUUID(it) },
            { exceptionReader(AppException.BadRequest) }
        )

    suspend fun getAllJedi() = JediService.getAllJedi()

}
