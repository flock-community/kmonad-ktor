package sith

import common.TestLayer
import community.flock.main
import community.flock.sith.define.Context
import community.flock.sith.moduleWith
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
            moduleWith(object : Context {
                override val sithRepository = TestLayer.sithRepository
                override val logger = TestLayer.logger
            })
        }) { block() }
    }

    private fun TestApplicationResponse.contains(s: String) = assertTrue(content?.contains(s) ?: false)
    private fun TestApplicationResponse.doesNotContain(s: String) = assertFalse(content?.contains(s) ?: true)

}
