package community.flock.jedi

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JediModuleTest {

    @Test
    fun getAllJedi() {
        withTestApplication({ moduleWithDependencies(TestJediRepository) }) {
            handleRequest(HttpMethod.Get, "/db").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content?.contains("Luke") ?: false, response.content)
                assertTrue(response.content?.contains("Yoda") ?: false, response.content)
            }
        }
    }

    @Test
    fun getOneJedi() {
        withTestApplication({ moduleWithDependencies(TestJediRepository) }) {
            handleRequest(HttpMethod.Get, "/db/${UUID.randomUUID()}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content?.contains("Luke") ?: false, response.content)
                assertFalse(response.content?.contains("Yoda") ?: true, response.content)
            }
        }
    }

}
