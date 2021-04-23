package community.flock.todo.pipe

import community.flock.AppException
import community.flock.todo.data.Todo
import community.flock.todo.define.Context
import java.util.UUID

suspend fun Context.bindGet() = getAll()

suspend fun Context.bindGet(uuidString: String?) = getByUUID(validate { UUID.fromString(uuidString) })

suspend fun Context.bindPost(todo: Todo) = save(todo)

suspend fun Context.bindDelete(uuidString: String?) = deleteByUUID(validate { UUID.fromString(uuidString) })

private fun <R> validate(block: () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest()
}
