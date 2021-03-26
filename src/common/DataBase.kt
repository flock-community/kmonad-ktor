package community.flock.common

import com.mongodb.ConnectionString
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


class DataBase private constructor(host: String) {

    val client = KMongo.createClient(ConnectionString("mongodb://$host")).coroutine

    companion object {
        @Volatile
        private var INSTANCE: DataBase? = null
        fun instance(host: String): DataBase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: DataBase(host).also { INSTANCE = it }
        }
    }
}
