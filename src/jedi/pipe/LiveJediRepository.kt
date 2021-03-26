package community.flock.jedi.pipe

import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import community.flock.AppException
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.JediRepository
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.UUID

class LiveJediRepository private constructor(private val collection: CoroutineCollection<Jedi>) : JediRepository {

    override suspend fun getJediByUUID(uuid: UUID) = guard { collection.findOne(Jedi::id eq uuid.toString()) }
        .flatMap { it?.let(::Right) ?: Left(AppException.NotFound(uuid)) }


    override suspend fun getAllJedi() = guard { collection.find().toFlow() }

    companion object {
        @Volatile
        private var INSTANCE: LiveJediRepository? = null
        fun instance(collection: CoroutineCollection<Jedi>): LiveJediRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveJediRepository(collection).also { INSTANCE = it }
        }
    }

}

private suspend fun <R> guard(block: suspend () -> R) = try {
    Right(block())
} catch (e: Exception) {
    Left(AppException.InternalServerError(e.cause))
}
