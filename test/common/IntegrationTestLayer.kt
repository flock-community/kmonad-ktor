package common

import com.mongodb.ConnectionString
import community.flock.kmonad.core.common.Logger
import org.junit.Assert.assertTrue
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import community.flock.droids.LiveContext as LiveDroidRepositoryContext
import community.flock.jedi.LiveContext as LiveJediRepositoryContext
import community.flock.sith.LiveContext as LiveSithRepositoryContext

object IntegrationTestLayer :
    LiveJediRepositoryContext,
    LiveSithRepositoryContext,
    LiveDroidRepositoryContext {

    override val databaseClient = KMongo.createClient(ConnectionString("mongodb://localhost:12345")).coroutine

    override val logger = object : Logger {

        override fun error(string: String) = assertTrue(string.isNotBlank())

        override fun log(string: String) = assertTrue(string.isNotBlank())

        override fun warn(string: String) = assertTrue(string.isNotBlank())

    }

}
