package community.flock.jedi

import arrow.core.Either
import arrow.core.Right
import arrow.core.flatMap
import community.flock.AppException
import community.flock.AppException.NotFound
import community.flock.exception
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestJediRepository : JediRepository {
    override suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi> = getAllJedi().flatMap { flow ->
        flow.firstOrNull { it.id === uuid.toString() }?.let { Right(it) } ?: exception(NotFound(uuid))
    }

    override suspend fun getAllJedi(): Either<AppException, Flow<Jedi>> =
        Right(flowOf(Jedi("Luke", 20), Jedi("Yoda", 942)))

}
