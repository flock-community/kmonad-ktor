package sith

import community.flock.sith.data.Sith
import community.flock.sith.define.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestRepository : Repository {
    override suspend fun getAll() = flowOf(Sith("Kasper", 32), Sith("Willem", 34))

    override suspend fun getByUUID(uuid: UUID) = getAll().first()

    override suspend fun save(sith: Sith) = sith

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid)
}
