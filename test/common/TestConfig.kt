package common

import community.flock.main
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import community.flock.droids.LiveRepository as LiveDroidRepository
import community.flock.droids.moduleWith as droidModuleWith
import community.flock.jedi.LiveRepository as LiveJediRepository
import community.flock.jedi.moduleWith as jediModuleWith
import community.flock.kmonad.core.droid.DroidContext
import community.flock.kmonad.core.jedi.JediContext
import community.flock.kmonad.core.sith.SithContext
import community.flock.kmonad.core.forcewielder.ForceWielderContext
import community.flock.sith.LiveRepository as LiveSithRepository
import community.flock.sith.moduleWith as sithModuleWith
import community.flock.wielders.moduleWith as wieldersModuleWith

@ExperimentalCoroutinesApi
fun setup(block: TestApplicationEngine.() -> TestApplicationCall) {
    withTestApplication({
        main()
        jediModuleWith(object : JediContext {
            override val jediRepository = LiveJediRepository(IntegrationTestLayer)
            override val logger = IntegrationTestLayer.logger
        })
        sithModuleWith(object : SithContext {
            override val sithRepository = LiveSithRepository(IntegrationTestLayer)
            override val logger = IntegrationTestLayer.logger
        })
        wieldersModuleWith(object : ForceWielderContext {
            override val jediRepository = LiveJediRepository(IntegrationTestLayer)
            override val sithRepository = LiveSithRepository(IntegrationTestLayer)
            override val logger = IntegrationTestLayer.logger
        })
        droidModuleWith(object : DroidContext {
            override val droidRepository = LiveDroidRepository(IntegrationTestLayer)
            override val logger = IntegrationTestLayer.logger
        })
    }) { block() }
}
