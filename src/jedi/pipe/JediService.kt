package community.flock.jedi.pipe

import arrow.core.Either
import community.flock.AppException
import community.flock.common.Reader
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.JediRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

object JediService {
    suspend fun getJediByUUID(uuid: UUID) =
        Reader<JediRepository, Either<AppException, Jedi>> { it.getJediByUUID(uuid) }

    suspend fun getAllJedi() = Reader<JediRepository, Either<AppException, Flow<Jedi>>> { it.getAllJedi() }
}
