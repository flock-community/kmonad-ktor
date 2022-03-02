package community.flock.todo.pipe

import community.flock.todo.data.Todo
import java.util.UUID

suspend fun <R> R.getAll() where R : HasTodoRepository = toDoRepository.getAll()

suspend fun <R> R.getByUUID(uuid: UUID) where R : HasTodoRepository = toDoRepository.getByUUID(uuid)

suspend fun <R> R.save(todo: Todo) where R : HasTodoRepository = toDoRepository.save(todo)

suspend fun <R> R.deleteByUUID(uuid: UUID) where R : HasTodoRepository = toDoRepository.deleteByUUID(uuid)
