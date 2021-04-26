package common

import community.flock.main
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import community.flock.jedi.define.Context as JediContext
import community.flock.jedi.moduleWith as jediModuleWith
import community.flock.sith.define.Context as SithContext
import community.flock.sith.moduleWith as sithModuleWith
import community.flock.todo.define.Context as ToDoContext
import community.flock.todo.moduleWith as toDoModuleWith
import community.flock.wielders.define.Context as ForceWieldersContext
import community.flock.wielders.moduleWith as forceWieldersModuleWith
import jedi.TestRepository as JediTestRepository
import sith.TestRepository as SithTestRepository
import todo.TestRepository as ToDoTestRepository

@ExperimentalCoroutinesApi
class ModuleTest {

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
                override val logger = TestLogger
                override val jediRepository = JediTestRepository
            })
            sithModuleWith(object : SithContext {
                override val logger = TestLogger
                override val sithRepository = SithTestRepository
            })
            forceWieldersModuleWith(object : ForceWieldersContext {
                override val logger = TestLogger
                override val jediRepository = JediTestRepository
                override val sithRepository = SithTestRepository
            })
            toDoModuleWith(object : ToDoContext {
                override val toDoRepository = ToDoTestRepository
            })
        }) { block() }
    }

}
