package community.flock.common

interface Logger {
    fun log(s: String)
    fun error()
    fun warn()
}