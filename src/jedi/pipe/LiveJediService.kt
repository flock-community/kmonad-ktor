package community.flock.jedi.pipe

import arrow.core.Either
import community.flock.AppException
import community.flock.jedi.JediService
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.Flow
import java.util.*

object LiveJediService : JediService {
    override suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi> {
        return LiveJediRepository.getJediByUUID(uuid)
    }

    override suspend fun getAllJedi(): Flow<Jedi> {
        return LiveJediRepository.getAllJedi()
    }
}
