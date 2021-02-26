package sith

import community.flock.common.Logger
import community.flock.main
import community.flock.sith.moduleWithDependencies
import community.flock.sith.pipe.Context
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertTrue

class SithModuleTest {


    @Test
    fun testModule() {
        withTestApplication({
            main()
            moduleWithDependencies(Context(TestSithRepository, object : Logger {
                override fun log(s: String) {
                    println(s)
                }

                override fun error() {
                    TODO("Not yet implemented")
                }

                override fun warn() {
                    TODO("Not yet implemented")
                }
            }))
        }) {
            handleRequest(HttpMethod.Get, "/sith").apply {
                assertTrue(response.content?.contains("Kasper") ?: false, response.content)
            }
        }
    }
}