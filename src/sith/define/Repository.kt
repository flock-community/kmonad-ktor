package community.flock.sith.define

import community.flock.common.define.Dependency
import community.flock.sith.data.Sith
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Repository : Dependency {
    suspend fun getAll(): Flow<Sith>

    suspend fun getByUUID(uuid: UUID): Sith

    suspend fun save(sith: Sith): Sith

    suspend fun deleteByUUID(uuid: UUID): Sith
}

interface HasRepository {
    val sithRepository: Repository
}
