package community.flock.jedi.pipe

import arrow.core.Either
import com.mongodb.ConnectionString
import community.flock.AppException
import community.flock.AppException.NotFound
import community.flock.exception
import community.flock.jedi.JediRepository
import community.flock.jedi.data.Jedi
import community.flock.jedi.data.internalize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import java.util.UUID

class LiveJediRepository private constructor(private val host: String) : JediRepository {

    private val collection by lazy {
        KMongo.createClient(ConnectionString("mongodb://$host")).coroutine
            .getDatabase("test")
            .getCollection<Jedi>().also {
                runBlocking { it.insertMany(listOf(Jedi("Luke", 19), Jedi("Yoda", 942))) }
            }
    }

    override suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi> = collection
        .findOne(Jedi::id eq uuid.toString())
        ?.internalize() ?: exception(NotFound(uuid))


    override suspend fun getAllJedi(): Either<AppException, Flow<Jedi>> =
        runCatching { collection.find().toFlow() }.internalize()

    companion object {
        @Volatile
        private var INSTANCE: LiveJediRepository? = null
        fun instance(host: String): LiveJediRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LiveJediRepository(host).also { INSTANCE = it }
        }
    }

}
