package community.flock.sith.pipe

import community.flock.sith.define.SithContext
import java.util.UUID

object SithService {

    suspend fun SithContext.getSithByUUID(uuid: UUID) = sithRepository.getSithByUUID(uuid)

    suspend fun SithContext.getAllSith() = sithRepository.getAllSith()
        .also { logger.log(it.toString()) };

}
