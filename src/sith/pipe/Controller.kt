package community.flock.sith.pipe

import community.flock.AppException
import community.flock.sith.data.Sith
import community.flock.sith.define.Context
import java.util.UUID

suspend fun Context.bindGet() = getAll()

suspend fun Context.bindGet(uuidString: String?) = getByUUID(validate { UUID.fromString(uuidString) })

suspend fun Context.bindPost(sith: Sith) = save(sith)

suspend fun Context.bindDelete(uuidString: String?) = deleteByUUID(validate { UUID.fromString(uuidString) })

private fun <R> validate(block: () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest()
}
