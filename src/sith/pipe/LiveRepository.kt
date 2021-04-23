package community.flock.sith.pipe

import community.flock.AppException
import community.flock.common.DataBase
import community.flock.common.define.DB
import community.flock.sith.data.Sith
import community.flock.sith.define.Repository
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.UUID

class LiveRepository private constructor(private val collection: CoroutineCollection<Sith>) : Repository {

    override suspend fun getAll() = guard { collection.find().toFlow() }

    override suspend fun getByUUID(uuid: UUID): Sith =
        guard { collection.findOne(Sith::id eq uuid.toString()) } ?: throw AppException.NotFound(uuid)

    override suspend fun save(sith: Sith) = guard { collection.insertOne(sith) }
        .run { if (wasAcknowledged()) sith else throw AppException.BadRequest() }

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid).let {
        guard { collection.deleteOne(Sith::id eq uuid.toString()) }
            .run { if (wasAcknowledged()) it else throw AppException.BadRequest() }
    }

    companion object {
        fun DataBase.liveRepository() = instance(client.getDatabase(DB.StarWars.name).getCollection())

        @Volatile
        private var INSTANCE: LiveRepository? = null
        private fun instance(collection: CoroutineCollection<Sith>): LiveRepository =
            INSTANCE ?: synchronized(this) { INSTANCE ?: LiveRepository(collection).also { INSTANCE = it } }
    }
}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.InternalServerError(e.cause)
}
