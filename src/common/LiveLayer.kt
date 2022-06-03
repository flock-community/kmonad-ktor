package community.flock.common

import com.mongodb.ConnectionString
import io.ktor.server.application.Application
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import community.flock.droids.LiveContext as LiveDroidRepositoryContext
import community.flock.jedi.LiveContext as LiveJediRepositoryContext
import community.flock.sith.LiveContext as LiveSithRepositoryContext

private interface Props {
    val host: String
    val port: Int
}

class LiveLayer private constructor(props: Props) :
    LiveJediRepositoryContext,
    LiveSithRepositoryContext,
    LiveDroidRepositoryContext {

    private val connectionString = ConnectionString("mongodb://${props.host}:${props.port}")

    override val databaseClient = KMongo.createClient(connectionString).coroutine
    override val logger = LiveLogger

    companion object {
        fun Application.getLayer() = instance(object : Props {
            override val host = getProp("ktor.db.host", "localhost")
            override val port = getProp("ktor.db.port", 27017)
        })

        @Volatile
        private var INSTANCE: LiveLayer? = null
        private fun instance(props: Props): LiveLayer =
            INSTANCE ?: synchronized(this) { INSTANCE ?: LiveLayer(props).also { INSTANCE = it } }
    }

}
