package community.flock.wielders.define

import community.flock.common.define.Logger
import community.flock.jedi.define.Context as JediContext
import community.flock.jedi.define.Repository as JediRepository
import community.flock.sith.define.Context as SithContext
import community.flock.sith.define.Repository as SithRepository

interface Context : JediContext, SithContext {
    override val jediRepository: JediRepository
    override val sithRepository: SithRepository
    override val logger: Logger
}
