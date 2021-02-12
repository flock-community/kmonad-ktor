package community.flock

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    val httpClient = HttpClient(Apache) {}

    val client = KMongo.createClient().coroutine

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/db") {
            call.respond(LiveJediRepository.getAllJedi().toList())
        }

        get("/db/{name}") {
            val name: String? = call.parameters["name"]

            if (name === null) {
                call.respond(Left(BadRequestException()))
            } else {
                call.respond(LiveJediRepository.getJediByName(name.capitalize()))
            }
        }
    }
}

data class Jedi(val name: String, val age: Int)

interface JediRepository {
    suspend fun getJediByName(name: String): Either<Exception, Jedi>

    suspend fun getAllJedi(): Flow<Jedi>
}

object LiveJediRepository : JediRepository {

    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("test") //normal java driver usage
    val col = database.getCollection<Jedi>() //KMongo extension method

    init {
        runBlocking { col.insertOne(Jedi("Luke", 19)) }
    }

    override suspend fun getJediByName(name: String): Either<Exception, Jedi> =
        col.findOne(Jedi::name eq name)?.let { Right(it) } ?: Left(NotFoundException(name))

    override suspend fun getAllJedi(): Flow<Jedi> = col.find().toFlow()
}

object TestJediRepository : JediRepository {
    override suspend fun getJediByName(name: String): Either<Exception, Jedi> =
        getAllJedi().firstOrNull { it.name === name }?.let { Right(it) } ?: Left(NotFoundException(name))

    override suspend fun getAllJedi(): Flow<Jedi> = flowOf(Jedi("Luke", 20), Jedi("Yoda", 942))
}


class NotFoundException(s: String) : Exception("$s Not found")
class BadRequestException : Exception("Bad Request")
