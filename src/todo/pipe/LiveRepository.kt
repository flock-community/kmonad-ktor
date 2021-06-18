package community.flock.todo.pipe

import community.flock.AppException.Conflict
import community.flock.AppException.InternalServerError
import community.flock.AppException.NotFound
import community.flock.common.define.DB
import community.flock.common.define.HasDatabaseClient
import community.flock.common.define.HasLogger
import community.flock.todo.data.PersistedTodo
import community.flock.todo.data.Todo
import community.flock.todo.data.internalize
import community.flock.todo.define.Repository
import kotlinx.coroutines.flow.map
import org.litote.kmongo.eq
import java.util.UUID

interface LiveRepositoryContext : HasDatabaseClient, HasLogger

class LiveRepository(ctx: LiveRepositoryContext) : Repository {

    private val collection = ctx.databaseClient.getDatabase(DB.Todos.name).getCollection<PersistedTodo>("todo")

    override suspend fun getAll() = guard { collection.find().toFlow() }
        .map { it.internalize() }

    override suspend fun getByUUID(uuid: UUID) = guard { collection.findOne(PersistedTodo::id eq uuid.toString()) }
        ?.internalize()
        ?: throw NotFound(uuid)

    override suspend fun save(todo: Todo): Todo {
        val uuid = UUID.fromString(todo.id)
        val exception = runCatching { getByUUID(uuid) }.exceptionOrNull() ?: throw Conflict(uuid)
        val result = if (exception is NotFound) guard { collection.insertOne(todo.externalize()) } else throw exception
        return if (result.wasAcknowledged()) todo else throw InternalServerError()
    }

    override suspend fun deleteByUUID(uuid: UUID): Todo {
        val todo = getByUUID(uuid)
        val result = guard { collection.deleteOne(PersistedTodo::id eq uuid.toString()) }
        return if (result.wasAcknowledged()) todo else throw InternalServerError()
    }

}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: Exception) {
    throw InternalServerError(e.cause)
}
