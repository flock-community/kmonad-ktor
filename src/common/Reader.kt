package community.flock.common

class Reader<D, out A>(val run: suspend (D) -> A) {

    inline fun <B> map(crossinline fa: (A) -> B): Reader<D, B> = Reader { d ->
        fa(run(d))
    }

    inline fun <B> flatMap(crossinline fa: (A) -> Reader<D, B>): Reader<D, B> = Reader { d ->
        fa(run(d)).run(d)
    }

    companion object Factory {
        fun <D, A> just(a: A): Reader<D, A> = Reader { _ -> a }

        fun <D> ask(): Reader<D, D> = Reader { it }
    }

}
