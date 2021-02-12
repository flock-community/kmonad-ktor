package community.flock.jedi.pipe

import arrow.core.getOrHandle
import community.flock.AppException.BadRequest
import community.flock.exception
import community.flock.jedi.JediController
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.toList
import java.util.*

object LiveJediController : JediController {
    override suspend fun getJediByUUID(uuidString: String?): Jedi = runCatching { UUID.fromString(uuidString) }
        .fold({ LiveJediService.getJediByUUID(it) }, { exception(BadRequest) })
        .getOrHandle { throw it }

    override suspend fun getAllJedi(): List<Jedi> = LiveJediService.getAllJedi().toList()
}
