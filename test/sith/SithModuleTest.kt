package sith

import common.TestLogger
import community.flock.common.define.Logger
import community.flock.main
import community.flock.sith.define.SithContext
import community.flock.sith.define.SithRepository
import community.flock.sith.moduleWith
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SithModuleTest {

    @Test
    fun testAllSith() = setup {
        handleRequest(HttpMethod.Get, "/sith").apply {
            response.contains("Kasper")
            response.contains("Willem")
        }
    }

    @Test
    fun testSithByUUID() = setup {
        handleRequest(HttpMethod.Get, "/sith/${UUID.randomUUID()}").apply {
            response.contains("Kasper")
            response.doesNotContain("Willem")
        }
    }

    private fun setup(block: TestApplicationEngine.() -> TestApplicationCall) {
        withTestApplication({
            main()
            moduleWith(object : SithContext {
                override val sithRepository: SithRepository = TestSithRepository
                override val logger: Logger = TestLogger
            })
        }) { block() }
    }

    private fun TestApplicationResponse.contains(s: String) = assertTrue(content?.contains(s) ?: false, content)
    private fun TestApplicationResponse.doesNotContain(s: String) = assertFalse(content?.contains(s) ?: true, content)

}
