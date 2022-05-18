package community.flock.sith

import com.mongodb.DuplicateKeyException
import com.mongodb.MongoException
import community.flock.common.DB
import community.flock.common.DB.StarWars
import community.flock.common.HasLive
import community.flock.kmonad.core.AppException.Conflict
import community.flock.kmonad.core.AppException.InternalServerError
import community.flock.kmonad.core.AppException.NotFound
import community.flock.kmonad.core.common.HasLogger
import community.flock.kmonad.core.sith.SithRepository
import community.flock.kmonad.core.sith.model.Sith
import org.litote.kmongo.eq
import java.util.UUID

interface LiveContext : HasLive.DatabaseClient, HasLogger

class LiveRepository(ctx: LiveContext) : SithRepository {

    private val collection = ctx.databaseClient.getDatabase(StarWars.name).getCollection<Sith>()
    private val logger = ctx.logger

    override suspend fun getAll() = runCatching {
        guard { collection.find().toList() }
    }

    override suspend fun getByUUID(uuid: UUID) = runCatching {
        guard { collection.findOne(Sith::id eq uuid.toString()) } ?: throw NotFound(uuid)
    }

    override suspend fun save(sith: Sith) = runCatching {
        val uuid = UUID.fromString(sith.id)
        val exception = getByUUID(uuid).also { logger.log(it.toString()) }.exceptionOrNull() ?: throw Conflict(uuid)
        val result = if (exception is NotFound) guard { collection.insertOne(sith) } else throw exception
        if (result.wasAcknowledged()) sith else throw InternalServerError()
    }

    override suspend fun deleteByUUID(uuid: UUID) = runCatching {
        val sith = getByUUID(uuid).getOrThrow()
        val result = guard { collection.deleteOne(Sith::id eq uuid.toString()) }
        if (result.wasAcknowledged()) sith else throw InternalServerError()
    }

}

private inline fun <A> guard(block: () -> A) = try {
    block()
} catch (e: DuplicateKeyException) {
    throw Conflict(null, e.cause)
} catch (e: MongoException) {
    throw InternalServerError(e.cause)
} catch (e: Exception) {
    throw InternalServerError(e.cause)
}
