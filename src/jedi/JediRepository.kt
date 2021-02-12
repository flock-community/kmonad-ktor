package community.flock.jedi

import arrow.core.Either
import community.flock.AppException
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.Flow
import java.util.*

interface JediRepository {
    suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi>

    suspend fun getAllJedi(): Flow<Jedi>
}
