package community.flock.sith.pipe

import community.flock.AppException
import community.flock.sith.define.SithContext
import java.util.UUID
import community.flock.sith.pipe.SithService.getAllSith as serviceGetAllSith
import community.flock.sith.pipe.SithService.getSithByUUID as serviceGetSithByUUID

object SithController {

    suspend fun SithContext.getSithByUUID(uuidString: String?) = runCatching { UUID.fromString(uuidString) }
        .getOrElse { throw AppException.BadRequest }
        .let { serviceGetSithByUUID(it) }


    suspend fun SithContext.getAllSith() = serviceGetAllSith()

}
