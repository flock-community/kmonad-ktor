package community.flock.sith

import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.LiveLogger
import community.flock.common.define.DB.StarWars
import community.flock.common.define.Logger
import community.flock.sith.data.Sith
import community.flock.sith.define.Context
import community.flock.sith.pipe.LiveRepository
import community.flock.sith.pipe.bindDelete
import community.flock.sith.pipe.bindGet
import community.flock.sith.pipe.bindPost
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.coroutines.flow.toList

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val host = getProp("ktor.db.host", "localhost")
    val collection = DataBase.instance(host).client.getDatabase(StarWars.name).getCollection<Sith>()

    moduleWith(object : Context {
        override val repository = LiveRepository.instance(collection)
        override val logger: Logger = LiveLogger
    })

}

fun Application.moduleWith(context: Context) {
    routing {
        get("/sith") {
            call.respond(context.bindGet().toList())
        }

        get("/sith/{uuid}") {
            call.respond(context.bindGet(call.parameters["uuid"]))
        }

        post("/sith") {
            call.respond(context.bindPost(call.receive()))
        }

        delete("/sith/{uuid}") {
            call.respond(context.bindDelete(call.parameters["uuid"]))
        }
    }
}
