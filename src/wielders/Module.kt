package community.flock.wielders

import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.kmonad.core.forcewielder.ForceWielderContext
import community.flock.kmonad.core.forcewielder.bindGet
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import community.flock.jedi.LiveRepository as LiveJediRepository
import community.flock.sith.LiveRepository as LiveSithRepository

@ExperimentalCoroutinesApi
@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : ForceWielderContext {
        override val jediRepository = LiveJediRepository(getLayer())
        override val sithRepository = LiveSithRepository(getLayer())
        override val logger = getLayer().logger
    })

}

@ExperimentalCoroutinesApi
fun Application.moduleWith(context: ForceWielderContext) {
    routing {
        route("/force-wielders") {
            get {
                call.respond(context.bindGet())
            }

            get("{uuid?}") {
                val uuidString = call.parameters["uuid"]
                call.respond(context.bindGet(uuidString))
            }
        }
    }
}
