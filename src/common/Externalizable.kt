package community.flock.common

interface Externalizable<T> {
    fun externalize(): T
}
