package community.flock.todo.pipe

import community.flock.AppException
import community.flock.common.DataBase
import community.flock.common.define.DB
import community.flock.todo.data.PersistedToDo
import community.flock.todo.data.ToDo
import community.flock.todo.data.internalize
import community.flock.todo.define.Repository
import kotlinx.coroutines.flow.map
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.UUID

class LiveRepository private constructor(private val collection: CoroutineCollection<PersistedToDo>) : Repository {

    override suspend fun getAll() = guard { collection.find().toFlow() }.map { it.internalize() }

    override suspend fun getByUUID(uuid: UUID) = guard {
        collection.findOne(PersistedToDo::id eq uuid.toString())?.internalize()
    } ?: throw AppException.NotFound(uuid)

    override suspend fun save(toDo: ToDo) = guard { collection.insertOne(toDo.externalize()) }
        .run { if (wasAcknowledged()) toDo else throw AppException.BadRequest() }

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid).let {
        guard { collection.deleteOne(PersistedToDo::id eq uuid.toString()) }
            .run { if (wasAcknowledged()) it else throw AppException.BadRequest() }
    }

    companion object {
        fun DataBase.liveRepository() = instance(client.getDatabase(DB.ToDos.name).getCollection())

        @Volatile
        private var INSTANCE: LiveRepository? = null
        private fun instance(collection: CoroutineCollection<PersistedToDo>): LiveRepository =
            INSTANCE ?: synchronized(this) { INSTANCE ?: LiveRepository(collection).also { INSTANCE = it } }
    }
}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.InternalServerError(e.cause)
}
