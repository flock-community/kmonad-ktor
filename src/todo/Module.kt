package community.flock.todo

import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.todo.data.consume
import community.flock.todo.pipe.Context
import community.flock.todo.pipe.LiveRepository
import community.flock.todo.pipe.bindDelete
import community.flock.todo.pipe.bindGet
import community.flock.todo.pipe.bindPost
import io.ktor.application.Application
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import community.flock.todo.Todo as PotentialTodo

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    moduleWith(object : Context {
        override val toDoRepository = LiveRepository(getLayer())
    })
}

fun Application.moduleWith(context: Context) {
    todoApi(object : Routes {
        override suspend fun get() = context.bindGet().map { it.produce() }.toList()
        override suspend fun getByUUID(uuid: String) = context.bindGet(uuid).produce()
        override suspend fun post(todo: PotentialTodo) = context.bindPost(todo.consume()).produce()
        override suspend fun deleteByUUID(uuid: String) = context.bindDelete(uuid).produce()
    })
}
