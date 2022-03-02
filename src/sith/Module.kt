package community.flock.sith

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.sith.Context
import community.flock.kmonad.core.sith.deleteByUUID
import community.flock.kmonad.core.sith.getAll
import community.flock.kmonad.core.sith.getByUUID
import community.flock.kmonad.core.sith.model.Sith
import community.flock.kmonad.core.sith.save
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import kotlinx.coroutines.flow.toList
import java.util.UUID

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : Context {
        override val sithRepository = LiveRepository(getLayer())
        override val logger = getLayer().logger
    })

}

fun Application.moduleWith(context: Context) {
    apiRouting {
        route("/sith")
            .throws(InternalServerError, AppException.InternalServerError::class)
            .throws(BadRequest, AppException.BadRequest::class)
            .throws(NotFound, AppException.NotFound::class)
            .throws(Conflict, AppException.Conflict::class) {
                get<Unit, List<Sith>> {
                    respond(context.getAll().toList())
                }

                get<UuidParam, Sith> {
                    val uuid = validate { UUID.fromString(it.uuidString) }
                    respond(context.getByUUID(uuid))
                }

                post<Unit, Sith, Sith> { _, sith ->
                    respond(context.save(sith))
                }

                delete<UuidParam, Sith> {
                    val uuid = validate { UUID.fromString(it.uuidString) }
                    respond(context.deleteByUUID(uuid))
                }
            }
    }
}

private fun <A> validate(block: () -> A) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest()
}

@Path("/{uuidString}")
data class UuidParam(@PathParam("UUID") val uuidString: String)
