package community.flock.jedi.pipe

import arrow.mtl.Kleisli
import community.flock.common.define.HasLogger
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.HasRepository
import kotlinx.coroutines.runBlocking
import java.util.UUID

suspend fun <D> getAll() where D : HasRepository, D : HasLogger = Kleisli { d: D ->
    runBlocking { d.jediRepository.getAll().apply { map { d.logger.log(it.toString()) } } }
}

suspend fun <D> getByUUID(uuid: UUID) where D : HasRepository = Kleisli { d: D ->
    runBlocking { d.jediRepository.getByUUID(uuid) }
}

suspend fun <D> save(jedi: Jedi) where D : HasRepository = Kleisli { d: D ->
    runBlocking { d.jediRepository.save(jedi) }
}

suspend fun <D> deleteByUUID(uuid: UUID) where D : HasRepository = Kleisli { d: D ->
    runBlocking { d.jediRepository.deleteByUUID(uuid) }
}
