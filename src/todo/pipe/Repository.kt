package community.flock.todo.pipe

import community.flock.todo.data.Todo
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface HasTodoRepository {
    val toDoRepository: Repository
}

interface Repository {

    suspend fun getAll(): Flow<Todo>

    suspend fun getByUUID(uuid: UUID): Todo

    suspend fun save(todo: Todo): Todo

    suspend fun deleteByUUID(uuid: UUID): Todo

}
