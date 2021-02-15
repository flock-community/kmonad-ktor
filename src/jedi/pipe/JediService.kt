package community.flock.jedi.pipe

import arrow.core.Either
import community.flock.AppException
import community.flock.common.Reader
import community.flock.jedi.JediRepository
import community.flock.jedi.data.Jedi
import java.util.UUID

object JediService {
    suspend fun getJediByUUID(uuid: UUID) =
        Reader<JediRepository, Either<AppException, Jedi>> { it.getJediByUUID(uuid) }

    suspend fun getAllJedi() = LiveJediRepository.getAllJedi()
}
