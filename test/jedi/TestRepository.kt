package jedi

import arrow.core.Either.Right
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestRepository : Repository {
    override suspend fun getAll() = Right(flowOf(Jedi(name = "Luke", age = 20), Jedi(name = "Yoda", age = 942)))

    override suspend fun getByUUID(uuid: UUID) = getAll().map { it.first() }

    override suspend fun save(jedi: Jedi) = Right(jedi)

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid)
}
