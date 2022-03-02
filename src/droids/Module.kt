package community.flock.droids

import arrow.core.Either
import arrow.core.getOrHandle
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.droids.model.Droid
import community.flock.kmonad.core.droids.Context
import community.flock.kmonad.core.droids.bindDelete
import community.flock.kmonad.core.droids.bindGet
import community.flock.kmonad.core.droids.bindPost
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.toList

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : Context {
        override val droidRepository = LiveRepository(getLayer())
    })

}

fun Application.moduleWith(context: Context) {
    apiRouting {
        route("/droids")
            .throws(HttpStatusCode.InternalServerError, AppException.InternalServerError::class)
            .throws(HttpStatusCode.BadRequest, AppException.BadRequest::class)
            .throws(HttpStatusCode.NotFound, AppException.NotFound::class)
            .throws(HttpStatusCode.Conflict, AppException.Conflict::class) {
                get<Unit, List<Droid>> {
                    handle { context.bindGet().map { it.toList() } }
                }

                get<UuidParam, Droid> { params ->
                    handle { context.bindGet(params.uuid) }
                }

                post<Unit, Droid, Droid> { _, body ->
                    handle { context.bindPost(body) }
                }

                delete<UuidParam, Droid> { params ->
                    handle { context.bindDelete(params.uuid) }
                }
            }
    }
}

@Path("/{uuid}")
data class UuidParam(@PathParam("UUID") val uuid: String)


private suspend inline fun <reified A : Any> OpenAPIPipelineResponseContext<A>.handle(block: () -> Either<AppException, A>) =
    respond(block().getOrHandle { throw it })
