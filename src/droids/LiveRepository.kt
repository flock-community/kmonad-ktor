package community.flock.droids

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import community.flock.common.DB
import community.flock.common.HasLive
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.droids.data.Droid
import community.flock.kmonad.core.droids.pipe.Repository
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.eq
import java.util.UUID

interface LiveContext : HasLive.DatabaseClient

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
class LiveRepository(ctx: LiveContext) : Repository {

    private val collection = ctx.databaseClient.getDatabase(DB.StarWars.name).getCollection<Droid>()


    override suspend fun getAll(): Either<AppException, Flow<Droid>> = guard { collection.find().toFlow() }

    override suspend fun getByUUID(uuid: UUID): Either<AppException, Droid> = either {
        val maybeDroid = guard { collection.findOne(Droid::id eq uuid.toString()) }.bind()
        maybeDroid ?: AppException.NotFound(uuid).left().bind()
    }

    override suspend fun save(droid: Droid): Either<AppException, Droid> = either {
        val uuid = UUID.fromString(droid.id)
        val existingDroid = getByUUID(uuid)
        if (existingDroid.isRight()) AppException.Conflict(uuid).left().bind() else {
            val result = guard { collection.insertOne(droid) }.bind()
            val maybeDroid = droid.takeIf { result.wasAcknowledged() }
            maybeDroid ?: AppException.InternalServerError().left().bind()
        }
    }

    override suspend fun deleteByUUID(uuid: UUID): Either<AppException, Droid> = either {
        val droid = getByUUID(uuid).bind()
        val result = guard { collection.deleteOne(Droid::id eq uuid.toString()) }.bind()
        val maybeDroid = droid.takeIf { result.wasAcknowledged() }
        maybeDroid ?: AppException.InternalServerError().left().bind()
    }

}

private inline fun <A> guard(block: () -> A) = guardWith(AppException::InternalServerError, block)

private inline fun <E : AppException, A> guardWith(errorBlock: (ex: Exception) -> E, block: () -> A) = try {
    block().right()
} catch (ex: Exception) {
    errorBlock(ex).left()
}
