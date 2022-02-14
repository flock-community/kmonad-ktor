package community.flock.jedi

import arrow.core.Either
import arrow.core.computations.EitherEffect
import arrow.core.left
import arrow.core.right
import community.flock.common.DB
import community.flock.common.HasLive
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.AppException.Conflict
import community.flock.kmonad.core.AppException.InternalServerError
import community.flock.kmonad.core.AppException.NotFound
import community.flock.kmonad.core.common.IO
import community.flock.kmonad.core.common.define.HasLogger
import community.flock.kmonad.core.jedi.data.Jedi
import community.flock.kmonad.core.jedi.pipe.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.eq
import java.util.UUID
import arrow.core.computations.either as eitherEffectFromArrow

interface LiveContext : HasLive.DatabaseClient, HasLogger

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
class LiveRepository(ctx: LiveContext) : Repository {

    private val collection = ctx.databaseClient.getDatabase(DB.StarWars.name).getCollection<Jedi>()


    override fun getAll(): IO<Either<InternalServerError, Flow<Jedi>>> = IO { getAllAsEither() }

    override fun getByUUID(uuid: UUID): IO<Either<AppException, Jedi>> = IO { getByUUIDAsEither(uuid) }

    override fun save(jedi: Jedi): IO<Either<AppException, Jedi>> = IO { saveAsEither(jedi) }

    override fun deleteByUUID(uuid: UUID): IO<Either<AppException, Jedi>> = IO { deleteByUUIDAsEither(uuid) }


    private fun getAllAsEither(): Either<InternalServerError, Flow<Jedi>> = guard { collection.find().toFlow() }

    private fun getByUUIDAsEither(uuid: UUID): Either<AppException, Jedi> = either {
        val maybeJedi = guard { collection.findOne(Jedi::id eq uuid.toString()) }.bind()
        maybeJedi ?: NotFound(uuid).left().bind()
    }

    private fun saveAsEither(jedi: Jedi): Either<AppException, Jedi> = either {
        val uuid = UUID.fromString(jedi.id)
        val existingJedi = getByUUIDAsEither(uuid)
        if (existingJedi.isRight()) Conflict(uuid).left().bind() else {
            val result = guard { collection.insertOne(jedi) }.bind()
            val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
            maybeJedi ?: InternalServerError().left().bind()
        }
    }

    private fun deleteByUUIDAsEither(uuid: UUID): Either<AppException, Jedi> = either {
        val jedi = getByUUIDAsEither(uuid).bind()
        val result = guard { collection.deleteOne(Jedi::id eq uuid.toString()) }.bind()
        val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
        maybeJedi ?: InternalServerError().left().bind()
    }

}


private inline fun <E, A> either(crossinline c: suspend EitherEffect<E, *>.() -> A) = runBlocking<Either<E, A>> {
    eitherEffectFromArrow { c() }
}

private inline fun <A> guard(block: () -> A) = guardWith(::InternalServerError, block)

private inline fun <E : AppException, A> guardWith(errorBlock: (ex: Exception) -> E, block: () -> A) = try {
    block().right()
} catch (ex: Exception) {
    errorBlock(ex).left()
}
