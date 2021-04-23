package community.flock.todo.pipe

import community.flock.todo.data.ToDo
import community.flock.todo.define.HasRepository
import java.util.UUID

suspend fun <D> D.getAll() where D : HasRepository = toDoRepository.getAll()

suspend fun <D> D.getByUUID(uuid: UUID) where D : HasRepository = toDoRepository.getByUUID(uuid)

suspend fun <D> D.save(toDo: ToDo) where D : HasRepository = toDoRepository.save(toDo)

suspend fun <D> D.deleteByUUID(uuid: UUID) where D : HasRepository = toDoRepository.deleteByUUID(uuid)
