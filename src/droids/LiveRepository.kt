package community.flock.droids

import arrow.core.continuations.EffectScope
import arrow.core.continuations.effect
import community.flock.common.DB
import community.flock.common.HasLive
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.AppException.Conflict
import community.flock.kmonad.core.AppException.InternalServerError
import community.flock.kmonad.core.AppException.NotFound
import community.flock.kmonad.core.droid.DroidRepository
import community.flock.kmonad.core.droid.model.Droid
import org.litote.kmongo.eq
import java.util.UUID

typealias HasAppException = EffectScope<AppException>


interface LiveContext : HasLive.DatabaseClient

class LiveRepository(ctx: LiveContext) : DroidRepository {

    private val collection = ctx.databaseClient.getDatabase(DB.StarWars.name).getCollection<Droid>()


    context(HasAppException)
    override suspend fun getAll() = guard { collection.find().toFlow() }

    context(HasAppException)
    override suspend fun getByUUID(uuid: UUID): Droid {
        val maybeDroid = guard { collection.findOne(Droid::id eq uuid.toString()) }
        return maybeDroid ?: shift(NotFound(uuid))
    }

    context(HasAppException)
    override suspend fun save(droid: Droid): Droid {
        val uuid = UUID.fromString(droid.id)
        val result = effect<AppException, Droid> {
            getByUUID(uuid)
        }.fold({
            val result = guard { collection.insertOne(droid) }
            val maybeDroid = droid.takeIf { result.wasAcknowledged() }
            maybeDroid ?: shift(InternalServerError())
        }, { shift(Conflict(uuid)) })
        return result
    }

    context(HasAppException)
    override suspend fun deleteByUUID(uuid: UUID): Droid {
        val droid = getByUUID(uuid)
        val result = guard { collection.deleteOne(Droid::id eq uuid.toString()) }
        val maybeDroid = droid.takeIf { result.wasAcknowledged() }
        return maybeDroid ?: shift(InternalServerError())
    }

}

context(HasAppException)
private suspend inline fun <A> guard(block: () -> A) = guardWith(AppException::InternalServerError, block)

context(HasAppException)
private suspend inline fun <E : AppException, A> guardWith(errorBlock: (ex: Exception) -> E, block: () -> A) =
    try {
        block()
    } catch (ex: Exception) {
        shift(errorBlock(ex))
    }
