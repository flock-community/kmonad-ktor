package community.flock.jedi.pipe

import community.flock.common.Reader.Factory.ask
import community.flock.jedi.define.JediRepository
import java.util.UUID

object JediService {
    suspend fun getJediByUUID(uuid: UUID) = ask<JediRepository>().map { it.getJediByUUID(uuid) }

    suspend fun getAllJedi() = ask<JediRepository>().map { it.getAllJedi() }
}
