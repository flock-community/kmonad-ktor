package todo

import community.flock.main
import community.flock.todo.pipe.Context
import community.flock.todo.moduleWith
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class ModuleTest {

    @Test
    fun testAllTodos() = setup {
        handleRequest(HttpMethod.Get, "/todo").apply {
            response.contains("ead3f222-1c30-49e4-bfda-5000c582b1d6")
            response.contains("5b11084b-ac6c-454f-9f4e-2be7c38a202b")
        }
    }

    @Test
    fun testTodoByUUID() = setup {
        handleRequest(HttpMethod.Get, "/todo/${UUID.randomUUID()}").apply {
            response.contains("ead3f222-1c30-49e4-bfda-5000c582b1d6")
            response.doesNotContain("5b11084b-ac6c-454f-9f4e-2be7c38a202b")
        }
    }

    private fun setup(block: TestApplicationEngine.() -> TestApplicationCall) {
        withTestApplication({
            main()
            moduleWith(object : Context {
                override val toDoRepository = TestRepository
            })
        }) { block() }
    }

    private fun TestApplicationResponse.contains(s: String) = assertTrue(content?.contains(s) ?: false)
    private fun TestApplicationResponse.doesNotContain(s: String) = assertFalse(content?.contains(s) ?: true)

}
