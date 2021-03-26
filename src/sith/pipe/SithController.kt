package community.flock.sith.pipe

import community.flock.AppException
import community.flock.sith.define.SithContext
import java.util.UUID
import community.flock.sith.pipe.SithService.getAllSith as serviceGetAllSith
import community.flock.sith.pipe.SithService.getSithByUUID as serviceGetSithByUUID

object SithController {

    suspend fun SithContext.getSithByUUID(uuidString: String?) =
        serviceGetSithByUUID(validate { UUID.fromString(uuidString) })

    suspend fun SithContext.getAllSith() = serviceGetAllSith()

}

private fun <R> validate(block: () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest
}
