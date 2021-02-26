package community.flock.sith.pipe

import community.flock.AppException
import community.flock.sith.data.Sith
import community.flock.sith.define.SithRepository
import org.litote.kmongo.coroutine.CoroutineCollection
import java.util.UUID

class LiveSithRepository private constructor(private val collection: CoroutineCollection<Sith>) : SithRepository {
    override suspend fun getSithByUUID(uuid: UUID): Sith =
        collection.findOneById(uuid.toString()) ?: throw AppException.NotFound(uuid)

    override suspend fun getAllSith() = collection.find().toFlow()

    companion object {
        @Volatile
        private var INSTANCE: LiveSithRepository? = null
        fun instance(collection: CoroutineCollection<Sith>): LiveSithRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveSithRepository(collection).also { INSTANCE = it }
        }
    }

}
