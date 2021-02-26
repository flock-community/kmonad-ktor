package sith

import community.flock.sith.data.Sith
import community.flock.sith.pipe.SithRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object TestSithRepository : SithRepository {
    override suspend fun getSithR(): Flow<Sith> = flowOf(Sith("Kasper", 32))
}
