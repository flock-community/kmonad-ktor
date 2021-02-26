package community.flock.sith.pipe

import community.flock.sith.data.Sith
import org.litote.kmongo.coroutine.CoroutineCollection

class LiveSithRepository private constructor(private val collection: CoroutineCollection<Sith>) : SithRepository {

    override suspend fun getSithR() = collection.find().toFlow()

    companion object {
        @Volatile
        private var INSTANCE: LiveSithRepository? = null
        fun instance(collection: CoroutineCollection<Sith>): LiveSithRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveSithRepository(collection).also { INSTANCE = it }
        }
    }

}
