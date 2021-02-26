package community.flock.sith.pipe

import community.flock.common.Logger

class Context(val repo: SithRepository, val logger: Logger)

object SithService {

    suspend fun Context.getSithS() = repo.getSithR()
        .also { logger.log(it.toString()) };

}
