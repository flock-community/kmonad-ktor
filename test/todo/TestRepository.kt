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
            id = UUID.randomUUID(),
            title = "toDo.title",
            description = "toDo.description",
            completed = false,
            createdAt = LocalDateTime.now(),
            dueDate = LocalDateTime.now()
        )
    )

    override suspend fun getByUUID(uuid: UUID) = getAll().first()

    override suspend fun save(todo: Todo) = todo

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid)
}