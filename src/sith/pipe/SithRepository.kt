package community.flock.sith.pipe

import community.flock.sith.data.Sith
import kotlinx.coroutines.flow.Flow

interface SithRepository {
    suspend fun getSithR(): Flow<Sith>
}
