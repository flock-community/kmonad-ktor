package community.flock.common

import community.flock.kmonad.core.common.Logger

object LiveLogger : Logger {

    override fun error(string: String) = System.err.println("ERROR: $string")

    override fun log(string: String) = println("LOG: $string")

    override fun warn(string: String) = println("WARN: $string")

}
