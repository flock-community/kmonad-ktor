package community.flock.jedi

import arrow.core.Either
import arrow.core.Right
import community.flock.AppException
import community.flock.jedi.data.Jedi
import community.flock.jedi.pipe.JediRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestJediRepository : JediRepository {
    override suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi> = getAllJedi()
        .map { it.first() }

    override suspend fun getAllJedi(): Either<AppException, Flow<Jedi>> =
        Right(flowOf(Jedi("Luke", 20), Jedi("Yoda", 942)))

}
