package community.flock.sith.define

import community.flock.common.define.Repository
import community.flock.sith.data.Sith
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SithRepository : Repository {
    suspend fun getSithByUUID(uuid: UUID): Sith

    suspend fun getAllSith(): Flow<Sith>
}
