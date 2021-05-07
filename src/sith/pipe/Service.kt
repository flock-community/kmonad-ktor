package community.flock.sith.pipe

import community.flock.common.define.HasLogger
import community.flock.sith.data.Sith
import community.flock.sith.define.HasRepository
import java.util.UUID



suspend fun <D> D.getAll() where D : HasRepository, D : HasLogger = sithRepository.getAll()
    .also { logger.log(it.toString()) }

suspend fun <D> D.getByUUID(uuid: UUID) where D : HasRepository = sithRepository.getByUUID(uuid)

suspend fun <D> D.save(sith: Sith) where D : HasRepository = sithRepository.save(sith)

suspend fun <D> D.deleteByUUID(uuid: UUID) where D : HasRepository = sithRepository.deleteByUUID(uuid)
