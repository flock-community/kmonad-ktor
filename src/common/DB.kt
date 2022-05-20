package community.flock.common

import org.litote.kmongo.coroutine.CoroutineClient

enum class DB { StarWars }

sealed interface HasLive {

    interface DatabaseClient {
        val databaseClient: CoroutineClient
    }

}
