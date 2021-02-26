package sith

import community.flock.sith.data.Sith
import community.flock.sith.define.SithRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestSithRepository : SithRepository {
    override suspend fun getSithByUUID(uuid: UUID): Sith = getAllSith().first()

    override suspend fun getAllSith(): Flow<Sith> = flowOf(Sith("Kasper", 32), Sith("Willem", 34))
}
