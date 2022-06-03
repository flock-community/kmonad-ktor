package community.flock.droids

import arrow.core.Either
import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.common.handleErrors
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.droid.DroidContext
import community.flock.kmonad.core.droid.bindDelete
import community.flock.kmonad.core.droid.bindGet
import community.flock.kmonad.core.droid.bindPost
import community.flock.kmonad.core.droid.model.Droid
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
import kotlinx.coroutines.flow.toList

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : DroidContext {
        override val droidRepository = LiveRepository(getLayer())
        override val logger = getLayer().logger
    })

}

fun Application.moduleWith(context: DroidContext) {
    routing {
        route("/droids") {
            get {
                handle { context.bindGet().toEither().map { it.toList() } }
            }

            get("{uuid?}") {
                val uuidString = call.parameters["uuid"]
                handle { context.bindGet(uuidString).toEither() }
            }

            post {
                val droid = call.receive<Droid>()
                handle { context.bindPost(droid).toEither() }
            }

            delete("{uuid?}") {
                val uuidString = call.parameters["uuid"]
                handle { context.bindDelete(uuidString).toEither() }
            }
        }
    }
}

private suspend inline fun <reified A : Any> PipelineContext<Unit, ApplicationCall>.handle(block: () -> Either<AppException, A>) =
    block().fold({ handleErrors(it) }, { call.respond(it) })
