package jedi

import arrow.core.Either
import arrow.core.right
import community.flock.kmonad.core.AppException
import community.flock.kmonad.core.common.IO
import community.flock.kmonad.core.jedi.data.Jedi
import community.flock.kmonad.core.jedi.pipe.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

object TestRepository : Repository {

    private val jedi = Jedi(name = "Luke", age = 23)

    override fun getAll(): IO<Either<AppException, Flow<Jedi>>> = IO { flowOf(jedi).right() }

    override fun getByUUID(uuid: UUID): IO<Either<AppException, Jedi>> = IO { jedi.right() }

    override fun save(jedi: Jedi): IO<Either<AppException, Jedi>> = IO { jedi.right() }

    override fun deleteByUUID(uuid: UUID) = getByUUID(uuid)

}
