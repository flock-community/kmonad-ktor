package common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import community.flock.kmonad.core.common.define.Data
import community.flock.kmonad.core.droids.model.Droid
import community.flock.kmonad.core.jedi.model.Jedi
import community.flock.kmonad.core.sith.model.Sith
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID

@ExperimentalCoroutinesApi
class IntegrationTest {

    @Test
    fun testJediModule() = setup {
        testCrud(
            "/jedi",
            Jedi(name = "Mace Windu", age = 54),
            Jedi(name = "Rey", age = 21)
        )
    }

    @Test
    fun testSithModule() = setup {
        testCrud(
            "/sith",
            Sith(name = "Darth Plagueis", age = 123),
            Sith(name = "Darth Sidious", age = 234)
        )
    }

    @Test
    fun testWieldersModule() = setup {
        val jedi = Jedi(name = "Mace Windu", age = 54)
        val sith = Sith(name = "Dart Sidiuous", age = 234)

        handleRequest(HttpMethod.Post, "/jedi") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(jedi.toJson())
        }

        handleRequest(HttpMethod.Post, "/sith") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(sith.toJson())
        }

        handleRequest(HttpMethod.Get, "/force-wielders").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains(jedi.id)
            response.contains("LIGHT")
            response.contains(sith.id)
            response.contains("DARK")
        }

        handleRequest(HttpMethod.Get, "/force-wielders/${jedi.id}").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains(jedi.id)
            response.contains("LIGHT")
            response.doesNotContain(sith.id)
            response.doesNotContain("DARK")
        }
    }

    @Test
    fun testDroidModule() = setup {
        testCrud(
            "/droids",
            Droid(designation = "4-LOM", type = Droid.Type.Protocol),
            Droid(designation = "R5-D4", type = Droid.Type.Astromech)
        )
    }

    @Test
    fun testTodoModule() = setup {
        testCrud(
            "/todo",
            Todo(
                title = "toDo.title",
                description = "toDo.description",
                completed = false,
                createdAt = LocalDateTime.now(),
                dueDate = LocalDateTime.now().plusDays(1L)
            ).produce(),
            Todo(
                title = "2nd.toDo.title",
                description = "2nd.toDo.description",
                completed = true,
                createdAt = LocalDateTime.now().minusDays(2L),
                dueDate = LocalDateTime.now().minusDays(1L)
            ).produce()
        )
    }

    private fun <T : Data> TestApplicationEngine.testCrud(
        resource: String,
        item1: T,
        item2: T
    ): TestApplicationCall {
        handleRequest(HttpMethod.Post, resource) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(item1.toJson())
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

        handleRequest(HttpMethod.Get, "${resource}/${UUID.randomUUID()}").apply {
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

    private fun Any.toJson() = jacksonObjectMapper().writeValueAsString(this)

    private fun TestApplicationResponse.contains(s: String) = assertTrue(content?.contains(s) ?: false)
    private fun TestApplicationResponse.doesNotContain(s: String) = assertFalse(content?.contains(s) ?: true)

}
