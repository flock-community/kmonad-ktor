package community.flock.todo.pipe

import community.flock.kmonad.core.AppException.BadRequest
import community.flock.todo.data.Todo
import java.util.UUID


interface Context : HasTodoRepository


suspend fun Context.bindGet() = getAll()

suspend fun Context.bindGet(uuidString: String?) = getByUUID(validate { UUID.fromString(uuidString) })

suspend fun Context.bindPost(todo: Todo) = save(todo)

suspend fun Context.bindDelete(uuidString: String?) = deleteByUUID(validate { UUID.fromString(uuidString) })


private fun <A> validate(block: () -> A) = try {
    block()
} catch (e: Exception) {
    throw BadRequest()
}
