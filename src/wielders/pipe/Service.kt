package community.flock.wielders.pipe

import arrow.core.extensions.either.monad.map
import arrow.core.getOrHandle
import community.flock.AppException
import community.flock.wielders.data.ForceWielder
import community.flock.wielders.data.toForceWielder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.util.UUID
import community.flock.jedi.define.Context as JediContext
import community.flock.jedi.pipe.getAll as getAllJedi
import community.flock.jedi.pipe.getByUUID as getJediByUUID
import community.flock.sith.define.Context as SithContext
import community.flock.sith.pipe.getAll as getAllSith
import community.flock.sith.pipe.getByUUID as getSithByUUID

@ExperimentalCoroutinesApi
suspend fun <D> D.getAll() where D : JediContext, D : SithContext = getAllJedi<JediContext>()
    .run(this)
    .getOrHandle { throw it }
    .map { it.toForceWielder() } + getAllSith()
    .map { it.toForceWielder() }

suspend fun <D> D.getByUUID(uuid: UUID) where D : JediContext, D : SithContext = (getJediByUUID<JediContext>(uuid)
    .run(this)
    .map { it.toForceWielder() }
    .orNull() to runCatching { getSithByUUID(uuid) }
    .map { it.toForceWielder() }
    .getOrNull())
    .run {
        when {
            bothAreNull() -> throw AppException.NotFound(uuid)
            bothAreNotNull() -> throw AppException.InternalServerError()
            else -> either()
        }
    }

@ExperimentalCoroutinesApi
private operator fun Flow<ForceWielder>.plus(f: Flow<ForceWielder>) = merge(this, f)

private fun <A : Any?, B : Any?> Pair<A, B>.bothAreNull(): Boolean = (first === null && second === null)
private fun <A : Any?, B : Any?> Pair<A, B>.bothAreNotNull(): Boolean = (first !== null && second !== null)
private fun <A> Pair<A?, A?>.either(): A = first ?: second!!
