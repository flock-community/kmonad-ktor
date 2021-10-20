package community.flock.wielders

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.wielders.data.ForceWielder
import community.flock.kmonad.core.wielders.pipe.Context
import community.flock.kmonad.core.wielders.pipe.bindGet
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import community.flock.jedi.LiveRepository as LiveJediRepository
import community.flock.sith.LiveRepository as LiveSithRepository

@ExperimentalCoroutinesApi
@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : Context {
        override val jediRepository = LiveJediRepository(getLayer())
        override val sithRepository = LiveSithRepository(getLayer())
        override val logger = getLayer().logger
    })

}

@ExperimentalCoroutinesApi
fun Application.moduleWith(context: Context) {
    apiRouting {
        route("/force-wielders")
            .throws(InternalServerError, AppException.InternalServerError::class)
            .throws(BadRequest, AppException.BadRequest::class)
            .throws(NotFound, AppException.NotFound::class)
            .throws(Conflict, AppException.Conflict::class) {
                get<Unit, List<ForceWielder>> {
                    respond(context.bindGet().toList())
                }

                get<UuidParam, ForceWielder> { params ->
                    respond(context.bindGet(params.uuid))
                }
            }
    }
}

@Path("/{uuid}")
data class UuidParam(@PathParam("UUID") val uuid: String)
