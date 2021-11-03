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
import community.flock.kmonad.core.droids.pipe.Context as DroidContext
import community.flock.kmonad.core.jedi.pipe.Context as JediContext
import community.flock.kmonad.core.sith.pipe.Context as SithContext
import community.flock.kmonad.core.wielders.pipe.Context as WieldersContext
import community.flock.sith.LiveRepository as LiveSithRepository
import community.flock.sith.moduleWith as sithModuleWith
import community.flock.todo.moduleWith as todoModuleWith
import community.flock.todo.pipe.Context as TodoContext
import community.flock.todo.pipe.LiveRepository as LiveTodoRepository
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
        wieldersModuleWith(object : WieldersContext {
            override val jediRepository = LiveJediRepository(IntegrationTestLayer)
            override val sithRepository = LiveSithRepository(IntegrationTestLayer)
            override val logger = IntegrationTestLayer.logger
        })
        droidModuleWith(object : DroidContext {
            override val droidRepository = LiveDroidRepository(IntegrationTestLayer)
            override val logger = IntegrationTestLayer.logger
        })
        todoModuleWith(object : TodoContext {
            override val toDoRepository = LiveTodoRepository(IntegrationTestLayer)
        })
    }) { block() }
}
