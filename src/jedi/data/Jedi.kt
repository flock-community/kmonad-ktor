package community.flock.jedi.data

import arrow.core.Right
import java.util.*

data class Jedi(val name: String, val age: Int) {
    val id: String = UUID.randomUUID().toString()
}

fun Jedi.internalize() = Right(this)
