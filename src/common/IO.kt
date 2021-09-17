package community.flock.common

class IO<out A>(val runUnsafe: suspend () -> A) {

    inline fun <B> map(crossinline f: suspend (A) -> B): IO<B> = IO { f(runUnsafe()) }

    inline fun <B> flatMap(crossinline f: suspend (A) -> IO<B>): IO<B> = IO { f(runUnsafe()).runUnsafe() }

}
