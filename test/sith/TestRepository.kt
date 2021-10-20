package sith

import community.flock.kmonad.core.sith.data.Sith
import community.flock.kmonad.core.sith.pipe.Repository
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestRepository : Repository {

    private val sith = Sith(name = "Darth Sidious", age = 86)

    override suspend fun getAll() = flowOf(sith)

    override suspend fun getByUUID(uuid: UUID) = sith

    override suspend fun save(sith: Sith) = sith

    override suspend fun deleteByUUID(uuid: UUID) = sith

}
