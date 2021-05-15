package community.flock.jedi.pipe

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.computations.either
import community.flock.AppException
import community.flock.AppException.BadRequest
import community.flock.AppException.Companion.conflict
import community.flock.AppException.Companion.notFound
import community.flock.AppException.InternalServerError
import community.flock.common.define.DB
import community.flock.common.define.HasDatabaseClient
import community.flock.common.define.HasLogger
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Repository
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.eq
import java.util.UUID

interface LiveRepositoryContext : HasDatabaseClient, HasLogger

class LiveRepository(ctx: LiveRepositoryContext) : Repository {

    private val collection = ctx.databaseClient.getDatabase(DB.StarWars.name).getCollection<Jedi>()

    override suspend fun getAll() = either<InternalServerError, Flow<Jedi>> {
        guard { collection.find().toFlow() }.bind()
    }

    override suspend fun getByUUID(uuid: UUID) = either<AppException, Jedi> {
        val maybeJedi = guard { collection.findOne(Jedi::id eq uuid.toString()) }.bind()
        guardWith(notFound(uuid)) { maybeJedi!! }.bind()
    }

    override suspend fun save(jedi: Jedi) = either<AppException, Jedi> {
        val result = guard { collection.insertOne(jedi) }.bind()
        val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
        guardWith(conflict(jedi.id)) { maybeJedi!! }.bind()
    }

    override suspend fun deleteByUUID(uuid: UUID) = either<AppException, Jedi> {
        val jedi = getByUUID(uuid).bind()
        val result = guard { collection.deleteOne(Jedi::id eq uuid.toString()) }.bind()
        val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
        guardWith(::BadRequest) { maybeJedi!! }.bind()
    }

}

private inline fun <R> guard(block: () -> R) = guardWith(::InternalServerError, block)

private inline fun <reified E : AppException, R> guardWith(errorBlock: (ex: Exception) -> E, block: () -> R) = try {
    Right(block())
} catch (ex: Exception) {
    Left(errorBlock(ex))
}
