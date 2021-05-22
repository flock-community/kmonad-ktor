package community.flock.todo

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.AppException
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode

interface Routes {
    suspend fun get(): List<Todo>
    suspend fun getByUUID(uuid: String): Todo
    suspend fun post(todo: Todo): Todo
    suspend fun deleteByUUID(uuid: String): Todo
}

fun Application.todoApi(routes: Routes) {
    apiRouting {
        route("/todo")
            .throws(HttpStatusCode.InternalServerError, AppException.InternalServerError::class)
            .throws(HttpStatusCode.BadRequest, AppException.BadRequest::class)
            .throws(HttpStatusCode.NotFound, AppException.NotFound::class)
            .throws(HttpStatusCode.Conflict, AppException.Conflict::class) {
                get<Unit, List<Todo>> { respond(routes.get()) }
                get<UuidParam, Todo> { respond(routes.getByUUID(it.uuid)) }
                post<Unit, Todo, Todo> { _, body -> respond(routes.post(body)) }
                delete<UuidParam, Todo> { respond(routes.deleteByUUID(it.uuid)) }
            }
    }
}

@Path("/{uuid}")
data class UuidParam(@PathParam("UUID") val uuid: String)

data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)
