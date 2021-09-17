package jedi

import arrow.core.Either
import arrow.core.Either.Right
import community.flock.AppException
import community.flock.common.IO
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestRepository : Repository {
    override fun getAll(): IO<Either<AppException, Flow<Jedi>>> =
        IO { Right(flowOf(Jedi(name = "Luke", age = 20), Jedi(name = "Yoda", age = 942))) }

    override fun getByUUID(uuid: UUID): IO<Either<AppException, Jedi>> = getAll().map { io -> io.map { it.first() } }

    override fun save(jedi: Jedi): IO<Either<AppException, Jedi>> = IO { Right(jedi) }

    override fun deleteByUUID(uuid: UUID) = getByUUID(uuid)
}
