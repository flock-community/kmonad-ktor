package common

import com.mongodb.ConnectionString
import io.ktor.application.Application
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import community.flock.jedi.pipe.LiveRepositoryContext as LiveJediRepositoryContext
import community.flock.sith.pipe.LiveRepositoryContext as LiveSithRepositoryContext
import community.flock.todo.pipe.LiveRepositoryContext as LiveTodoRepositoryContext

class IntegrationTestLayer private constructor() :
    LiveJediRepositoryContext,
    LiveSithRepositoryContext,
    LiveTodoRepositoryContext {

    override val databaseClient = KMongo.createClient(ConnectionString("mongodb://localhost:12345")).coroutine
    override val logger = TestLogger

    companion object {
        fun Application.getLayer() = instance()

        @Volatile
        private var INSTANCE: IntegrationTestLayer? = null
        private fun instance(): IntegrationTestLayer =
            INSTANCE ?: synchronized(this) { INSTANCE ?: IntegrationTestLayer().also { INSTANCE = it } }
    }

}

