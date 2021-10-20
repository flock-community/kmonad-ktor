package community.flock.todo.pipe

import community.flock.common.define.Has
import community.flock.todo.data.Todo

import java.util.UUID

suspend fun <R> R.getAll() where R : Has.TodoRepository = toDoRepository.getAll()

suspend fun <R> R.getByUUID(uuid: UUID) where R : Has.TodoRepository = toDoRepository.getByUUID(uuid)

suspend fun <R> R.save(todo: Todo) where R : Has.TodoRepository = toDoRepository.save(todo)

suspend fun <R> R.deleteByUUID(uuid: UUID) where R : Has.TodoRepository = toDoRepository.deleteByUUID(uuid)
