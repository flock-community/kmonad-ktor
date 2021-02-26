package community.flock.sith.pipe

import community.flock.sith.pipe.SithService.getSithS

object SithController {
    suspend fun Context.getSithC() = getSithS()
}
