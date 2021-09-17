package community.flock.jedi.define

import arrow.core.Either
import community.flock.AppException
import community.flock.common.IO
import community.flock.common.define.Dependency
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Repository : Dependency {
    fun getAll(): IO<Either<AppException, Flow<Jedi>>>

    fun getByUUID(uuid: UUID): IO<Either<AppException, Jedi>>

    fun save(jedi: Jedi): IO<Either<AppException, Jedi>>

    fun deleteByUUID(uuid: UUID): IO<Either<AppException, Jedi>>
}

interface HasRepository {
    val jediRepository: Repository
}
