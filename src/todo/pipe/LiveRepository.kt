package community.flock.todo.pipe

import community.flock.AppException
import community.flock.common.define.DB
import community.flock.common.define.HasDatabaseClient
import community.flock.common.define.HasLogger
import community.flock.todo.data.PersistedToDo
import community.flock.todo.data.internalize
import community.flock.todo.define.Repository
import kotlinx.coroutines.flow.map
import org.litote.kmongo.eq
import java.util.UUID

interface LiveRepositoryContext : HasDatabaseClient, HasLogger

class LiveRepository(ctx: LiveRepositoryContext) : Repository {

    private val collection = ctx.databaseClient.getDatabase(DB.ToDos.name).getCollection<PersistedToDo>()

    override suspend fun getAll() =
        guard { collection.find().toFlow() }.map { it.internalize() }

    override suspend fun getByUUID(uuid: UUID) = guard {
        collection.findOne(PersistedToDo::id eq uuid.toString())?.internalize()
    } ?: throw AppException.NotFound(uuid)

    override suspend fun save(todo: community.flock.todo.data.Todo) = guard { collection.insertOne(todo.externalize()) }
        .run { if (wasAcknowledged()) todo else throw AppException.BadRequest() }

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid).let {
        guard { collection.deleteOne(PersistedToDo::id eq uuid.toString()) }
            .run { if (wasAcknowledged()) it else throw AppException.BadRequest() }
    }

}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.InternalServerError(e.cause)
}
