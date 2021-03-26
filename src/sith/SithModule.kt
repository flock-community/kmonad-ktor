package community.flock.sith

import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.LiveLogger
import community.flock.common.define.DB.StarWars
import community.flock.common.define.Logger
import community.flock.sith.data.Sith
import community.flock.sith.define.SithContext
import community.flock.sith.define.SithRepository
import community.flock.sith.pipe.LiveSithRepository
import community.flock.sith.pipe.SithController.getAllSith
import community.flock.sith.pipe.SithController.getSithByUUID
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.flow.toList

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val host = getProp("ktor.db.host", "localhost")
    val sithCollection = DataBase.instance(host).client.getDatabase(StarWars.name).getCollection<Sith>()

    moduleWith(object : SithContext {
        override val sithRepository: SithRepository = LiveSithRepository.instance(sithCollection)
        override val logger: Logger = LiveLogger
    })

}

fun Application.moduleWith(ctx: SithContext) {

    routing {
        get("/sith") {
            call.respond(ctx.getAllSith().toList())
        }

        get("/sith/{uuid}") {
            call.respond(ctx.getSithByUUID(call.parameters["uuid"]))
        }
    }

}
