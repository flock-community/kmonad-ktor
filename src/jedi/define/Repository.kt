package community.flock.jedi.define

import arrow.core.Either
import community.flock.AppException
import community.flock.common.define.Dependency
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Repository : Dependency {
    suspend fun getAll(): Either<AppException, Flow<Jedi>>

    suspend fun getByUUID(uuid: UUID): Either<AppException, Jedi>

    suspend fun save(jedi: Jedi): Either<AppException, Jedi>

    suspend fun deleteByUUID(uuid: UUID): Either<AppException, Jedi>
}

interface HasRepository {
    val jediRepository: Repository
}
