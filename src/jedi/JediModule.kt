package community.flock.jedi

import com.mongodb.ConnectionString
import community.flock.common.Env.getProp
import community.flock.jedi.data.Jedi
import community.flock.jedi.pipe.JediController
import community.flock.jedi.pipe.LiveJediRepository
import community.flock.respond
import community.flock.respondFlow
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    val host = getProp("ktor.db.host", "localhost")
    val mongoDbClient = KMongo.createClient(ConnectionString("mongodb://$host")).coroutine
    val jediCollection = mongoDbClient.getDatabase("test").getCollection<Jedi>().also {
        runBlocking { it.insertMany(listOf(Jedi("Luke", 19), Jedi("Yoda", 942))) }
    }

    val jediRepository = LiveJediRepository.instance(jediCollection)

    routing {
        get("/db") {
            respondFlow(jediRepository) { JediController.getAllJedi() }
        }

        get("/db/{uuid}") {
            respond(jediRepository) { JediController.getJediByUUID(call.parameters["uuid"]) }
        }
    }
}
