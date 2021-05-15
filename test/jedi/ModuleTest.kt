package jedi

import common.TestLayer
import community.flock.jedi.define.Context
import community.flock.jedi.moduleWith
import community.flock.main
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModuleTest {

    @Test
    fun getAllJedi() = setup {
        handleRequest(HttpMethod.Get, "/jedi").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains("Luke")
            response.contains("Yoda")
        }
    }


    @Test
    fun getOneJedi() = setup {
        handleRequest(HttpMethod.Get, "/jedi/${UUID.randomUUID()}").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            response.contains("Luke")
            response.doesNotContain("Yoda")
        }
    }


    private fun setup(block: TestApplicationEngine.() -> TestApplicationCall) {
        withTestApplication({
            main()
            moduleWith(object : Context {
                override val jediRepository = TestLayer.jediRepository
                override val logger = TestLayer.logger
            })
        }) { block() }
    }

    private fun TestApplicationResponse.contains(s: String) = assertTrue(content?.contains(s) ?: false, content)
    private fun TestApplicationResponse.doesNotContain(s: String) = assertFalse(content?.contains(s) ?: true, content)

}
