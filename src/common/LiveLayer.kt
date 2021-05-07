package community.flock.common

import com.mongodb.ConnectionString
import community.flock.common.Env.getProp
import io.ktor.application.Application
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import community.flock.jedi.pipe.LiveRepositoryContext as LiveJediRepositoryContext
import community.flock.sith.pipe.LiveRepositoryContext as LiveSithRepositoryContext
import community.flock.todo.pipe.LiveRepositoryContext as LiveToDoRepositoryContext

private interface Props {
    val host: String
}

class LiveLayer private constructor(props: Props) :
    LiveJediRepositoryContext,
    LiveSithRepositoryContext,
    LiveToDoRepositoryContext {

    override val databaseClient = KMongo.createClient(ConnectionString("mongodb://${props.host}")).coroutine
    override val logger = LiveLogger

    companion object {
        fun Application.getLayer() = instance(object : Props {
            override val host = getProp("ktor.db.host", "localhost")
        })

        @Volatile
        private var INSTANCE: LiveLayer? = null
        private fun instance(props: Props): LiveLayer =
            INSTANCE ?: synchronized(this) { INSTANCE ?: LiveLayer(props).also { INSTANCE = it } }
    }

}
