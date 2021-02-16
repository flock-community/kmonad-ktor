package community.flock.common

import io.ktor.application.Application

object Env {
    fun Application.getProp(property: String, default: String): String =
        environment.config.propertyOrNull(property)?.getString() ?: default
}
