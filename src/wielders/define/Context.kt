package community.flock.wielders.define

import community.flock.common.define.HasLogger
import community.flock.common.define.Logger
import community.flock.sith.define.HasRepository
import community.flock.jedi.define.HasRepository as HasJediRepository
import community.flock.jedi.define.Repository as JediRepository
import community.flock.sith.define.Repository as SithRepository

interface Context: HasJediRepository, HasRepository, HasLogger {
    override val jediRepository: JediRepository
    override val sithRepository: SithRepository
    override val logger: Logger
}
