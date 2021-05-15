package community.flock.common.define

interface Externalizable<T> {
    fun externalize(): T
}
