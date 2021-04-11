package community.flock.jedi.pipe

import community.flock.common.Reader.Factory.ask
import community.flock.common.define.HasLogger
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.HasRepository
import java.util.UUID

suspend fun <D> getAllJedi() where D : HasRepository, D : HasLogger = ask<D>()
    .map { d -> d.repository.getAll().apply { map { d.logger.log(it.toString()) } } }

suspend fun <D> getJediByUUID(uuid: UUID) where D : HasRepository = ask<D>()
    .map { it.repository.getByUUID(uuid) }

suspend fun <D> save(jedi: Jedi) where D : HasRepository = ask<D>()
    .map { it.repository.save(jedi) }

suspend fun <D> deleteByUUID(uuid: UUID) where D : HasRepository = ask<D>()
    .map { it.repository.deleteByUUID(uuid) }
