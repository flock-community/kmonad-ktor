package community.flock.sith.pipe

import com.mongodb.DuplicateKeyException
import com.mongodb.MongoException
import community.flock.AppException.Conflict
import community.flock.AppException.InternalServerError
import community.flock.AppException.NotFound
import community.flock.common.define.DB
import community.flock.common.define.HasDatabaseClient
import community.flock.common.define.HasLogger
import community.flock.jedi.pipe.LiveRepositoryContext
import community.flock.sith.data.Sith
import community.flock.sith.define.Repository
import org.litote.kmongo.eq
import java.util.UUID

interface LiveRepositoryContext : HasDatabaseClient, HasLogger

class LiveRepository(ctx: LiveRepositoryContext) : Repository {

    private val collection = ctx.databaseClient.getDatabase(DB.StarWars.name).getCollection<Sith>()

    override suspend fun getAll() = guard { collection.find().toFlow() }

    override suspend fun getByUUID(uuid: UUID): Sith = guard { collection.findOne(Sith::id eq uuid.toString()) }
        ?: throw NotFound(uuid)

    override suspend fun save(sith: Sith): Sith {
        val uuid = UUID.fromString(sith.id)
        val exception = runCatching { getByUUID(uuid) }.exceptionOrNull() ?: throw Conflict(uuid)
        val result = if (exception is NotFound) guard { collection.insertOne(sith) } else throw exception
        return if (result.wasAcknowledged()) sith else throw InternalServerError()
    }

    override suspend fun deleteByUUID(uuid: UUID): Sith {
        val sith = getByUUID(uuid)
        val result = guard { collection.deleteOne(Sith::id eq uuid.toString()) }
        return if (result.wasAcknowledged()) sith else throw InternalServerError()
    }

}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: DuplicateKeyException) {
    throw Conflict(null, e.cause)
} catch (e: MongoException) {
    throw InternalServerError(e.cause)
} catch (e: Exception) {
    throw InternalServerError(e.cause)
}
