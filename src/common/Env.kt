package community.flock.common

import io.ktor.application.Application

object Env {
    fun Application.mongoDbHost() = environment.config.propertyOrNull("ktor.db.host")?.getString() ?: "localhost"
}
