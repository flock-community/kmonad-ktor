package community.flock.sith.pipe

import community.flock.AppException
import community.flock.sith.pipe.Service.bindGetSithByUUID
import community.flock.sith.pipe.Service.getAllSith
import java.util.UUID

object Controller {

    interface GetSithByUUID : Service.GetSithByUUID

    suspend fun GetSithByUUID.bindGetSithByUUID(uuidString: String?) =
        bindGetSithByUUID(validate { UUID.fromString(uuidString) })

    interface GetAllSith : Service.GetAllSith

    suspend fun GetAllSith.bindGetAllSith() = getAllSith()

}

private fun <R> validate(block: () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest
}
