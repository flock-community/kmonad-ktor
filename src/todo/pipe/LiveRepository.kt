package community.flock.todo.pipe

import community.flock.AppException
import community.flock.todo.data.Todo
import community.flock.todo.define.Repository
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.UUID

class LiveRepository private constructor(private val collection: CoroutineCollection<Todo>) : Repository {

    override suspend fun getAll(): Flow<Todo> {
        println("================================================== ${collection.collection} ==================================================")
        return guard { collection.find().toFlow() }.also { println("test") }
    }

    override suspend fun getByUUID(uuid: UUID) =
        guard { collection.findOne(Todo::id eq uuid.toString()) } ?: throw AppException.NotFound(uuid)

    override suspend fun save(todo: Todo) = guard { collection.insertOne(todo) }
        .run { if (wasAcknowledged()) todo else throw AppException.BadRequest() }

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid).let {
        guard { collection.deleteOne(Todo::id eq uuid.toString()) }
            .run { if (wasAcknowledged()) it else throw AppException.BadRequest() }
    }

    companion object {
        @Volatile
        private var INSTANCE: LiveRepository? = null
        fun instance(collection: CoroutineCollection<Todo>): LiveRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveRepository(collection).also { INSTANCE = it }
        }
    }
}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.InternalServerError(e.cause)
}
