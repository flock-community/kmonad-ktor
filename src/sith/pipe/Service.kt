package community.flock.sith.pipe

import community.flock.common.define.HasLogger
import community.flock.sith.define.HasSithRepository
import java.util.UUID

object Service {

    interface GetSithByUUID : HasSithRepository

    suspend fun GetSithByUUID.bindGetSithByUUID(uuid: UUID) = repository.getSithByUUID(uuid)

    interface GetAllSith : HasSithRepository, HasLogger

    suspend fun GetAllSith.getAllSith() = repository.getAllSith()
        .also { logger.log(it.toString()) };

}
