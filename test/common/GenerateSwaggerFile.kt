package common

import community.flock.main
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import community.flock.jedi.LiveRepository as LiveJediRepository
import community.flock.jedi.moduleWith as jediModuleWith
import community.flock.kmonad.core.jedi.pipe.Context as JediContext
import community.flock.kmonad.core.sith.pipe.Context as SithContext
import community.flock.kmonad.core.wielders.pipe.Context as ForceWieldersContext
import community.flock.sith.LiveRepository as LiveSithRepository
import community.flock.sith.moduleWith as sithModuleWith
import community.flock.todo.moduleWith as toDoModuleWith
import community.flock.todo.pipe.Context as ToDoContext
import community.flock.todo.pipe.LiveRepository as LiveTodoRepository
import community.flock.wielders.moduleWith as forceWieldersModuleWith

@ExperimentalCoroutinesApi
class GenerateSwaggerFile {

    @Test
    fun generateSwaggerJson() = setup {
        handleRequest(HttpMethod.Get, "/spec/swagger.json").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            File("build/swagger.json").writeText(response.content!!)
        }
    }

    private fun setup(block: TestApplicationEngine.() -> TestApplicationCall) {
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
            forceWieldersModuleWith(object : ForceWieldersContext {
                override val jediRepository = LiveJediRepository(IntegrationTestLayer)
                override val sithRepository = LiveSithRepository(IntegrationTestLayer)
                override val logger = IntegrationTestLayer.logger
            })
            toDoModuleWith(object : ToDoContext {
                override val toDoRepository = LiveTodoRepository(IntegrationTestLayer)
            })
        }) { block() }
    }

}
