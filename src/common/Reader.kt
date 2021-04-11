package community.flock.common

class Reader<D, out A>(val run: suspend (D) -> A) {

    inline fun <B> map(crossinline fa: suspend (A) -> B): Reader<D, B> = Reader { fa(run(it)) }

    inline fun <B> flatMap(crossinline fa: (A) -> Reader<D, B>): Reader<D, B> = Reader { fa(run(it)).run(it) }

    companion object Factory {
        fun <D, A> just(a: A): Reader<D, A> = Reader { a }

        fun <D> ask(): Reader<D, D> = Reader { it }
    }

}
