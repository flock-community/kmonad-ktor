package community.flock.todo.pipe

import community.flock.AppException
import community.flock.todo.data.ToDo
import community.flock.todo.define.Context
import java.util.UUID

suspend fun Context.bindGet() = getAll()

suspend fun Context.bindGet(uuidString: String?) = getByUUID(validate { UUID.fromString(uuidString) })

suspend fun Context.bindPost(toDo: ToDo) = save(toDo)

suspend fun Context.bindDelete(uuidString: String?) = deleteByUUID(validate { UUID.fromString(uuidString) })

private fun <R> validate(block: () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest()
}
