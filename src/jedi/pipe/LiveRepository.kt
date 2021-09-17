package community.flock.jedi.pipe

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import community.flock.AppException
import community.flock.AppException.Conflict
import community.flock.AppException.InternalServerError
import community.flock.AppException.NotFound
import community.flock.common.IO
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

    override fun getAll(): IO<Either<InternalServerError, Flow<Jedi>>> = IO {
        getAllAsEither()
    }

    private fun getAllAsEither(): Either<InternalServerError, Flow<Jedi>> = guard { collection.find().toFlow() }

    override fun getByUUID(uuid: UUID): IO<Either<AppException, Jedi>> = IO {
        getByUUIDAsEither(uuid)
    }

    private suspend fun getByUUIDAsEither(uuid: UUID): Either<AppException, Jedi> = either {
        val maybeJedi = !guard { collection.findOne(Jedi::id eq uuid.toString()) }
        maybeJedi ?: !NotFound(uuid).left()
    }

    override fun save(jedi: Jedi): IO<Either<AppException, Jedi>> = IO {
        saveAsEither(jedi)
    }

    private suspend fun saveAsEither(jedi: Jedi): Either<AppException, Jedi> = either {
        val uuid = UUID.fromString(jedi.id)
        val existingJedi = getByUUIDAsEither(uuid)
        if (existingJedi.isRight()) !Conflict(uuid).left() else {
            val result = !guard { collection.insertOne(jedi) }
            val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
            maybeJedi ?: !InternalServerError().left()
        }
    }

    override fun deleteByUUID(uuid: UUID): IO<Either<AppException, Jedi>> = IO {
        deleteByUUIDAsEither(uuid)
    }

    private suspend fun deleteByUUIDAsEither(uuid: UUID): Either<AppException, Jedi> = either {
        val jedi = !getByUUIDAsEither(uuid)
        val result = !guard { collection.deleteOne(Jedi::id eq uuid.toString()) }
        val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
        maybeJedi ?: !InternalServerError().left()
    }

}

private inline fun <R> guard(block: () -> R) = guardWith(::InternalServerError, block)

private inline fun <E : AppException, R> guardWith(errorBlock: (ex: Exception) -> E, block: () -> R) = try {
    block().right()
} catch (ex: Exception) {
    errorBlock(ex).left()
}
