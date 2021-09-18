package common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import common.IntegrationTestLayer.Companion.getLayer
import community.flock.common.define.Data
import community.flock.jedi.data.Jedi
import community.flock.main
import community.flock.sith.data.Sith
import community.flock.todo.data.Todo
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import community.flock.jedi.define.Context as JediContext
import community.flock.jedi.moduleWith as jediModuleWith
import community.flock.jedi.pipe.LiveRepository as LiveJediRepository
import community.flock.sith.define.Context as SithContext
import community.flock.sith.moduleWith as sithModuleWith
import community.flock.sith.pipe.LiveRepository as LiveSithRepository
import community.flock.todo.define.Context as TodoContext
import community.flock.todo.moduleWith as todoModuleWith
import community.flock.todo.pipe.LiveRepository as LiveTodoRepository

class IntegrationTest {

    @Test
    fun testJediModule() = setup {
        val jedi1 = Jedi(id = "4e1affbd-4001-45e7-a741-dd97f2ace15c", name = "Mace Windu", age = 54)
        val jedi2 = Jedi(id = "fb055e9d-0009-4759-ac85-dae908c67c70", name = "Rey", age = 21)
        testCrud("/jedi", jedi1, jedi2)
    }

    @Test
    fun testSithModule() = setup {
        val sith1 = Sith(id = "265548a9-9170-45dd-af4e-1e57e009be28", name = "Darth Plagueis", age = 123)
        val sith2 = Sith(id = "2a0d17c7-dcc8-4495-a2e6-96126c6ffc8a", name = "Darth Sidious", age = 234)
        testCrud("/sith", sith1, sith2)
    }

    @Test
    fun testTodoModule() = setup {
        val todo1 = Todo(
            id = "265548a9-9170-45dd-af4e-1e57e009be28",
            title = "toDo.title",
            description = "toDo.description",
            completed = false,
            createdAt = LocalDateTime.now(),
            dueDate = LocalDateTime.now().plusDays(1L)
        )
        val todo2 = Todo(
            id = "2a0d17c7-dcc8-4495-a2e6-96126c6ffc8a",
            title = "2nd.toDo.title",
            description = "2nd.toDo.description",
            completed = true,
            createdAt = LocalDateTime.now().minusDays(2L),
            dueDate = LocalDateTime.now().minusDays(1L)
        )
        testCrud("/todo", todo1.expose(), todo2.expose())
    }

    private inline fun <reified T> TestApplicationEngine.testCrud(
        resource: String,
        item1: T,
        item2: T
    ): TestApplicationCall where T : Data {
        handleRequest(HttpMethod.Post, resource) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(jacksonObjectMapper().writeValueAsString(item1))
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains(item1.id)
            response.doesNotContain(item2.id)
        }

        handleRequest(HttpMethod.Post, resource) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(item2.toJson())
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains(item2.id)
            response.doesNotContain(item1.id)
        }

        handleRequest(HttpMethod.Post, resource) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(item2.toJson())
        }.apply {
            assertEquals(HttpStatusCode.Conflict, response.status())
            assertNull(response.content)
        }

        handleRequest(HttpMethod.Get, resource).apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains(item1.id)
            response.contains(item2.id)
        }

        handleRequest(HttpMethod.Get, "${resource}/${item1.id}").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains(item1.id)
            response.doesNotContain(item2.id)
        }

        handleRequest(HttpMethod.Get, "${resource}/29cd10cb-70f3-457e-a2a4-983b06690562").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }

        handleRequest(HttpMethod.Delete, "${resource}/${item2.id}").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains(item2.id)
            response.doesNotContain(item1.id)
        }

        return handleRequest(HttpMethod.Delete, "${resource}/${item2.id}").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun setup(block: TestApplicationEngine.() -> TestApplicationCall) {
        withTestApplication({
            main()
            jediModuleWith(object : JediContext {
                override val jediRepository = LiveJediRepository(getLayer())
                override val logger = getLayer().logger
            })
            sithModuleWith(object : SithContext {
                override val sithRepository = LiveSithRepository(getLayer())
                override val logger = getLayer().logger
            })
            todoModuleWith(object : TodoContext {
                override val toDoRepository = LiveTodoRepository(getLayer())
            })
        }) { block() }
    }

    private fun Any.toJson() = jacksonObjectMapper().writeValueAsString(this)

    private fun TestApplicationResponse.contains(s: String) = assertTrue(content?.contains(s) ?: false)
    private fun TestApplicationResponse.doesNotContain(s: String) = assertFalse(content?.contains(s) ?: true)

}