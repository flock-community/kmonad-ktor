package community.flock.jedi.data

import arrow.core.Right
import community.flock.internalize
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class Jedi(val name: String, val age: Int) {
    val id: String = UUID.randomUUID().toString()
}

fun Jedi.internalize() = Right(this)
fun Flow<Jedi>.internalize() = Right(this)
fun Result<Flow<Jedi>>.internalize() = fold({it.internalize()}, {it.internalize()})
