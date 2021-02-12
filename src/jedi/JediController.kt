package community.flock.jedi

import community.flock.jedi.data.Jedi

interface JediController {
    suspend fun getJediByUUID(uuidString: String?): Jedi

    suspend fun getAllJedi(): List<Jedi>
}
