package community.flock.wielders.pipe

import community.flock.AppException
import community.flock.wielders.define.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.UUID

@ExperimentalCoroutinesApi
suspend fun Context.bindGet() = getAll()

suspend fun Context.bindGet(uuidString: String?) = getByUUID(validate { UUID.fromString(uuidString) })

private fun <R> validate(block: () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest(e)
}
