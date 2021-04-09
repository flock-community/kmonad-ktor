package community.flock.sith.pipe

import community.flock.AppException
import community.flock.sith.data.Sith
import community.flock.sith.define.SithRepository
import org.litote.kmongo.coroutine.CoroutineCollection
import java.util.UUID

class LiveRepository private constructor(private val collection: CoroutineCollection<Sith>) : SithRepository {
    override suspend fun getSithByUUID(uuid: UUID): Sith =
        guard { collection.findOneById(uuid.toString()) } ?: throw AppException.NotFound(uuid)

    override suspend fun getAllSith() = guard { collection.find().toFlow() }

    companion object {
        @Volatile
        private var INSTANCE: LiveRepository? = null
        fun instance(collection: CoroutineCollection<Sith>): LiveRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveRepository(collection).also { INSTANCE = it }
        }
    }

}

private suspend fun <R> guard(block: suspend () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.InternalServerError(e.cause)
}
