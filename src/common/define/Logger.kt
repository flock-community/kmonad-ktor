package community.flock.common.define

interface Logger : Dependency {
    fun log(s: String)
    fun error()
    fun warn()
}
