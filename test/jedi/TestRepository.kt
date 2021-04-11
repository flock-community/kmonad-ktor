package jedi

import arrow.core.Either
import arrow.core.Right
import community.flock.AppException
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestRepository : Repository {
    override suspend fun getAll(): Either<AppException, Flow<Jedi>> =
        Right(flowOf(Jedi("Luke", 20), Jedi("Yoda", 942)))

    override suspend fun getByUUID(uuid: UUID): Either<AppException, Jedi> = getAll()
        .map { it.first() }

    override suspend fun save(jedi: Jedi): Either<AppException, Jedi> = Right(jedi)

    override suspend fun deleteByUUID(uuid: UUID): Either<AppException, Jedi> = getByUUID(uuid)
}
