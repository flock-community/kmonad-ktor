package community.flock.todo.define

import community.flock.common.define.Dependency
import community.flock.todo.data.ToDo
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Repository : Dependency {
    suspend fun getAll(): Flow<ToDo>

    suspend fun getByUUID(uuid: UUID): ToDo

    suspend fun save(toDo: ToDo): ToDo

    suspend fun deleteByUUID(uuid: UUID): ToDo
}

interface HasRepository {
    val toDoRepository: Repository
}
