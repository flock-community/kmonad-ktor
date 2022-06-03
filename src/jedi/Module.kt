package community.flock.jedi

import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.common.handleErrors
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.common.monads.getOrHandle
import community.flock.kmonad.core.jedi.JediContext
import community.flock.kmonad.core.jedi.bindDelete
import community.flock.kmonad.core.jedi.bindGet
import community.flock.kmonad.core.jedi.bindPost
import community.flock.kmonad.core.jedi.model.Jedi
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : JediContext {
        override val jediRepository = LiveRepository(getLayer())
        override val logger = getLayer().logger
    })

}

fun Application.moduleWith(context: JediContext) {
    routing {
        route("/jedi") {
            get {
                bindGet()
                    .provide(context)
                    .runUnsafe()
                    .fold({handleErrors(it)}, {call.respond(it)})
            }

            get("{uuid?}") {
                val uuidString = call.parameters["uuid"]
                bindGet(uuidString)
                    .provide(context)
                    .runUnsafe()
                    .fold({handleErrors(it)}, {call.respond(it)})
            }

            post {
                val jedi = call.receive<Jedi>()
                bindPost(jedi)
                    .provide(context)
                    .runUnsafe()
                    .fold({handleErrors(it)}, {call.respond(it)})

            }

            delete("{uuid?}") {
                val uuidString = call.parameters["uuid"]
                bindDelete(uuidString)
                    .provide(context)
                    .runUnsafe()
                    .fold({handleErrors(it)}, {call.respond(it)})

            }
        }
    }
}
