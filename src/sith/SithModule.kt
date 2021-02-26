package community.flock.sith

import com.mongodb.ConnectionString
import community.flock.common.Env.getProp
import community.flock.common.Logger
import community.flock.sith.data.Sith
import community.flock.sith.pipe.Context
import community.flock.sith.pipe.LiveSithRepository
import community.flock.sith.pipe.SithController.getSithC
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    val host = getProp("ktor.db.host", "localhost")
    val mongoDbClient = KMongo.createClient(ConnectionString("mongodb://$host")).coroutine
    val sithCollection = mongoDbClient.getDatabase("test").getCollection<Sith>().also {
        runBlocking { it.insertMany(listOf(Sith("Palpatine", 340), Sith("Anakin", 29))) }
    }
    val context = Context(LiveSithRepository.instance(sithCollection), object : Logger {
        override fun log(s: String) {
            println(s)
        }

        override fun error() {
            TODO("Not yet implemented")
        }

        override fun warn() {
            TODO("Not yet implemented")
        }
    })
    moduleWithDependencies(context)
}

fun Application.moduleWithDependencies(ctx: Context) {

    routing {
        get("/sith") {
            ctx.getSithC().let {
                call.respond(it.toList())
            }
        }
    }

}
