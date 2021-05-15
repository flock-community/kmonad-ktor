package community.flock.todo

import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.AppException
import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.common.UuidParam
import community.flock.todo.data.ExposedTodo
import community.flock.todo.data.PotentialTodo
import community.flock.todo.data.consume
import community.flock.todo.define.Context
import community.flock.todo.pipe.LiveRepository
import community.flock.todo.pipe.bindDelete
import community.flock.todo.pipe.bindGet
import community.flock.todo.pipe.bindPost
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

typealias Ctx = PipelineContext<Unit, ApplicationCall>

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : Context {
        override val toDoRepository = LiveRepository(getLayer())
    })

}

fun Application.moduleWith(context: Context) {
    apiRouting {
        route("/todo")
            .throws(InternalServerError, AppException.InternalServerError::class)
            .throws(BadRequest, AppException.BadRequest::class)
            .throws(NotFound, AppException.NotFound::class)
            .throws(Conflict, AppException.Conflict::class) {
                get<Unit, List<ExposedTodo>> {
                    respond(context.bindGet().map { it.expose() }.toList())
                }

                get<UuidParam, ExposedTodo> { params ->
                    respond(context.bindGet(params.uuid).expose())
                }

                post<Unit, ExposedTodo, PotentialTodo> { _, body ->
                    respond(context.bindPost(body.consume()).expose())
                }

                delete<UuidParam, ExposedTodo> { params ->
                    respond(context.bindDelete(params.uuid).expose())
                }
            }
    }
}
