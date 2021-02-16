package community.flock.jedi.pipe

import arrow.core.Either
import community.flock.AppException
import community.flock.AppException.NotFound
import community.flock.exception
import community.flock.jedi.data.Jedi
import community.flock.jedi.data.internalize
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.UUID

class LiveJediRepository private constructor(private val collection: CoroutineCollection<Jedi>) : JediRepository {

    override suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi> = collection
        .findOne(Jedi::id eq uuid.toString())
        ?.internalize() ?: exception(NotFound(uuid))


    override suspend fun getAllJedi(): Either<AppException, Flow<Jedi>> =
        runCatching { collection.find().toFlow() }.internalize()

    companion object {
        @Volatile
        private var INSTANCE: LiveJediRepository? = null
        fun instance(collection: CoroutineCollection<Jedi>): LiveJediRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveJediRepository(collection).also { INSTANCE = it }
        }
    }

}
