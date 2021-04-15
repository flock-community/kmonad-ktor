package community.flock.wielders.pipe

import arrow.core.getOrHandle
import community.flock.AppException
import community.flock.wielders.data.ForceWielder
import community.flock.wielders.define.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.util.UUID
import community.flock.jedi.define.Context as JediContext
import community.flock.jedi.pipe.getAll as getAllJedi
import community.flock.jedi.pipe.getByUUID as getJediByUUID
import community.flock.sith.pipe.getAll as getAllSith
import community.flock.sith.pipe.getByUUID as getSithByUUID

@ExperimentalCoroutinesApi
suspend fun Context.bindGet(): Flow<ForceWielder> = getAllJedi<JediContext>()
    .run(this)
    .getOrHandle { throw it }
    .map { ForceWielder(it.name, it.age, it.id) } + getAllSith()
    .map { ForceWielder(it.name, it.age, it.id) }

@ExperimentalCoroutinesApi
suspend fun Context.bindGet(uuidString: String?): ForceWielder {
    val uuid = validate { UUID.fromString(uuidString) }

    val jedi = getJediByUUID<JediContext>(uuid)
        .run(this)
        .map { ForceWielder(it.name, it.age, it.id) }
        .orNull()

    val sith = runCatching { getSithByUUID(uuid) }
        .map { ForceWielder(it.name, it.age, it.id) }
        .getOrNull()

    val both = jedi to sith

    return when {
        both.areNull() -> throw AppException.NotFound(uuid)
        both.areNotNull() -> throw AppException.InternalServerError()
        else -> both.either()
    }
}

@ExperimentalCoroutinesApi
private operator fun Flow<ForceWielder>.plus(f: Flow<ForceWielder>) = merge(this, f)

private fun <R> validate(block: () -> R) = try {
    block()
} catch (e: Exception) {
    throw AppException.BadRequest(e)
}

private fun <A : Any?, B : Any?> Pair<A, B>.areNull(): Boolean = (first === null && second === null)
private fun <A : Any?, B : Any?> Pair<A, B>.areNotNull(): Boolean = (first !== null && second !== null)
private fun <A> Pair<A?, A?>.either(): A = first ?: second!!
