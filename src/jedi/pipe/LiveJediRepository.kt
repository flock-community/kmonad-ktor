package community.flock.jedi.pipe

import arrow.core.Either
import arrow.core.Right
import community.flock.AppException
import community.flock.AppException.NotFound
import community.flock.exception
import community.flock.jedi.JediRepository
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import java.util.*

object LiveJediRepository : JediRepository {

    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("test") //normal java driver usage
    val col = database.getCollection<Jedi>() //KMongo extension method

    init {
        runBlocking { col.insertOne(Jedi("Luke", 19)) }
    }

    override suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi> =
        col.findOne(Jedi::id eq uuid.toString())?.let { Right(it) } ?: exception(NotFound(uuid))

    override suspend fun getAllJedi(): Flow<Jedi> = col.find().toFlow()
}
