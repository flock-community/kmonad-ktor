package community.flock.sith.pipe

import community.flock.AppException
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

    override suspend fun getByUUID(uuid: UUID): Sith =
        guard { collection.findOne(Sith::id eq uuid.toString()) } ?: throw AppException.NotFound(uuid)

    override suspend fun save(sith: Sith) = guard { collection.insertOne(sith) }
        .run { if (wasAcknowledged()) sith else throw AppException.BadRequest() }

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid).let {
        guard { collection.deleteOne(Sith::id eq uuid.toString()) }
            .run { if (wasAcknowledged()) it else throw AppException.BadRequest() }
    }

}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.InternalServerError(e.cause)
}
