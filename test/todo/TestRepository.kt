package todo

import community.flock.todo.data.Todo
import community.flock.todo.define.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.util.UUID

object TestRepository : Repository {
    override suspend fun getAll() = flowOf(
        Todo(
            id = "ead3f222-1c30-49e4-bfda-5000c582b1d6",
            title = "toDo.title",
            description = "toDo.description",
            completed = false,
            createdAt = LocalDateTime.now(),
            dueDate = LocalDateTime.now().plusDays(1L)
        ),
        Todo(
            id = "5b11084b-ac6c-454f-9f4e-2be7c38a202b",
            title = "2nd.toDo.title",
            description = "2nd.toDo.description",
            completed = true,
            createdAt = LocalDateTime.now().minusDays(2L),
            dueDate = LocalDateTime.now().minusDays(1L)
        )
    )

    override suspend fun getByUUID(uuid: UUID) = getAll().first()

    override suspend fun save(todo: Todo) = todo

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid)
}
