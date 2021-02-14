package community.flock.jedi.pipe

import arrow.core.getOrHandle
import community.flock.AppException.BadRequest
import community.flock.exception
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.toList
import java.util.*

object JediController {
    suspend fun getJediByUUID(uuidString: String?): Jedi = runCatching { UUID.fromString(uuidString) }
        .fold({ JediService.getJediByUUID(it) }, { exception(BadRequest) })
        .getOrHandle { throw it }

    suspend fun getAllJedi(): List<Jedi> = JediService.getAllJedi().toList()
}
