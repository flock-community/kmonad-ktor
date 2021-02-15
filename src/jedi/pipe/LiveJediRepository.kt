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

object LiveJediRepository : JediRepository {

    var host: String? = null

    private val col by lazy {
        KMongo.createClient(ConnectionString("mongodb://$host")).coroutine
            .getDatabase("test")
            .getCollection<Jedi>().also {
                runBlocking { it.insertOne(community.flock.jedi.data.Jedi("Luke", 19)) }
            }
    }

    override suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi> = col
        .findOne(Jedi::id eq uuid.toString())
        ?.internalize() ?: exception(NotFound(uuid))


    override suspend fun getAllJedi(): Flow<Jedi> = col
        .find()
        .toFlow()

}
