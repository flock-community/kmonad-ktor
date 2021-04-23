package community.flock.todo.pipe

import community.flock.todo.data.Todo
import community.flock.todo.define.HasRepository
import java.util.UUID

suspend fun <D> D.getAll() where D : HasRepository = toDoRepository.getAll()

suspend fun <D> D.getByUUID(uuid: UUID) where D : HasRepository = toDoRepository.getByUUID(uuid)

suspend fun <D> D.save(todo: Todo) where D : HasRepository = toDoRepository.save(todo)

suspend fun <D> D.deleteByUUID(uuid: UUID) where D : HasRepository = toDoRepository.deleteByUUID(uuid)
