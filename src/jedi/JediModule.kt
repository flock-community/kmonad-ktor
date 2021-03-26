package community.flock.jedi

import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.define.DB.StarWars
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.JediRepository
import community.flock.jedi.pipe.JediController
import community.flock.jedi.pipe.LiveJediRepository
import community.flock.respond
import community.flock.respondFlow
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.get
import io.ktor.routing.routing

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val host = getProp("ktor.db.host", "localhost")
    val jediCollection = DataBase.instance(host).client.getDatabase(StarWars.name).getCollection<Jedi>()

    moduleWithDependencies(LiveJediRepository.instance(jediCollection))

}

fun Application.moduleWithDependencies(repository: JediRepository) {

    routing {
        get("/jedi") {
            respondFlow(repository) { JediController.getAllJedi() }
        }

        get("/jedi/{uuid}") {
            respond(repository) { JediController.getJediByUUID(call.parameters["uuid"]) }
        }
    }

}
