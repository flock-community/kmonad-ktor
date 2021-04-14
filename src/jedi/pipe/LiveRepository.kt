package community.flock.jedi.pipe

import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import community.flock.AppException
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Repository
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.UUID

class LiveRepository private constructor(private val collection: CoroutineCollection<Jedi>) : Repository {

    override suspend fun getAll() = guard { collection.find().toFlow() }

    override suspend fun getByUUID(uuid: UUID) = guard { collection.findOne(Jedi::id eq uuid.toString()) }
        .flatMap { it?.let(::Right) ?: Left(AppException.NotFound(uuid)) }

    override suspend fun save(jedi: Jedi) = guard { collection.insertOne(jedi) }
        .flatMap { if (it.wasAcknowledged()) Right(jedi) else Left(AppException.BadRequest()) }

    override suspend fun deleteByUUID(uuid: UUID) = getByUUID(uuid).flatMap { jedi ->
        guard { collection.deleteOne(Jedi::id eq uuid.toString()) }.flatMap {
            if (it.wasAcknowledged()) Right(jedi) else Left(AppException.BadRequest())
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LiveRepository? = null
        fun instance(collection: CoroutineCollection<Jedi>): LiveRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveRepository(collection).also { INSTANCE = it }
        }
    }

}

private suspend fun <R> guard(block: suspend () -> R) = try {
    Right(block())
} catch (e: Exception) {
    Left(AppException.InternalServerError(e.cause))
}
