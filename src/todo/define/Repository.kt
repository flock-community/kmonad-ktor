package community.flock.todo.define

import community.flock.common.define.Dependency

import community.flock.todo.data.Todo
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Repository : Dependency {
    suspend fun getAll(): Flow<Todo>

    suspend fun getByUUID(uuid: UUID): Todo

    suspend fun save(todo: Todo): Todo

    suspend fun deleteByUUID(uuid: UUID): Todo
}

interface HasRepository {
    val toDoRepository: Repository
}
