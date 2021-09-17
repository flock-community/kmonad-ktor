package community.flock.jedi.pipe

import arrow.core.Either.Left
import arrow.core.Either.Right
import community.flock.AppException
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Context
import community.flock.toReader
import java.util.UUID


fun bindGet() = getAll<Context>()

fun bindGet(uuidString: String?) = validate { UUID.fromString(uuidString) }
    .fold({ it.toReader() }, { getByUUID<Context>(it) })

fun bindPost(jedi: Jedi) = save<Context>(jedi)

fun bindDelete(uuidString: String?) = validate { UUID.fromString(uuidString) }
    .fold({ it.toReader() }, { deleteByUUID<Context>(it) })


private fun <R> validate(block: () -> R) = try {
    Right(block())
} catch (e: Exception) {
    Left(AppException.BadRequest())
}
