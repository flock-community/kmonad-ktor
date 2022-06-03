package community.flock.sith

import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.common.handleErrors
import community.flock.kmonad.core.sith.SithContext
import community.flock.kmonad.core.sith.bindDelete
import community.flock.kmonad.core.sith.bindGet
import community.flock.kmonad.core.sith.bindPost
import community.flock.kmonad.core.sith.model.Sith
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : SithContext {
        override val sithRepository = LiveRepository(getLayer())
        override val logger = getLayer().logger
    })

}

fun Application.moduleWith(context: SithContext) {
    routing {
        route("/sith") {
            get {
                context.bindGet().fold({ call.respond(it) }, { handleErrors(it) })
            }

            get("{uuid?}") {
                val uuidString = call.parameters["uuid"]
                context.bindGet(uuidString).fold({ call.respond(it) }, { handleErrors(it) })
            }

            post {
                val sith = call.receive<Sith>()
                context.bindPost(sith).fold({ call.respond(it) }, { handleErrors(it) })
            }

            delete("{uuid?}") {
                val uuidString = call.parameters["uuid"]
                context.bindDelete(uuidString).fold({ call.respond(it) }, { handleErrors(it) })
            }
        }
    }
}
