package common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import community.flock.commonModule
import community.flock.kmonad.core.common.Data
import community.flock.kmonad.core.droid.DroidContext
import community.flock.kmonad.core.droid.model.Droid
import community.flock.kmonad.core.forcewielder.ForceWielderContext
import community.flock.kmonad.core.jedi.JediContext
import community.flock.kmonad.core.jedi.model.Jedi
import community.flock.kmonad.core.sith.SithContext
import community.flock.kmonad.core.sith.model.Sith
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import community.flock.droids.LiveRepository as LiveDroidRepository
import community.flock.droids.moduleWith as droidModuleWith
import community.flock.jedi.LiveRepository as LiveJediRepository
import community.flock.jedi.moduleWith as jediModuleWith
import community.flock.sith.LiveRepository as LiveSithRepository
import community.flock.sith.moduleWith as sithModuleWith
import community.flock.wielders.moduleWith as wieldersModuleWith

@ExperimentalCoroutinesApi
class IntegrationTest {

    @Test
    fun testJediModule() = testApplication {
        application {
            commonModule()
            jediModuleWith(object : JediContext {
                override val jediRepository = LiveJediRepository(IntegrationTestLayer)
                override val logger = IntegrationTestLayer.logger
            })
        }
        testCrud(
            "/jedi",
            Jedi(name = "Mace Windu", age = 54),
            Jedi(name = "Rey", age = 21)
        )
    }

    @Test
    fun testSithModule() = testApplication {
        application {
            commonModule()
            sithModuleWith(object : SithContext {
                override val sithRepository = LiveSithRepository(IntegrationTestLayer)
                override val logger = IntegrationTestLayer.logger
            })
        }
        testCrud(
            "/sith",
            Sith(name = "Darth Plagueis", age = 123),
            Sith(name = "Darth Sidious", age = 234)
        )
    }

    @Test
    fun testWieldersModule() = testApplication {
        application {
            commonModule()
            jediModuleWith(object : JediContext {
                override val jediRepository = LiveJediRepository(IntegrationTestLayer)
                override val logger = IntegrationTestLayer.logger
            })
            sithModuleWith(object : SithContext {
                override val sithRepository = LiveSithRepository(IntegrationTestLayer)
                override val logger = IntegrationTestLayer.logger
            })
            wieldersModuleWith(object : ForceWielderContext {
                override val jediRepository = LiveJediRepository(IntegrationTestLayer)
                override val sithRepository = LiveSithRepository(IntegrationTestLayer)
                override val logger = IntegrationTestLayer.logger
            })
        }

        val jedi = Jedi(name = "Mace Windu", age = 54)
        val sith = Sith(name = "Dart Sidiuous", age = 234)

        client.post("/jedi") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(jedi.toJson())
        }

        client.post("/sith") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(sith.toJson())
        }

        client.get("/force-wielders").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(jedi.id))
            assertTrue(bodyAsText().contains("LIGHT"))
            assertTrue(bodyAsText().contains(sith.id))
            assertTrue(bodyAsText().contains("DARK"))
        }

        client.get("/force-wielders/${jedi.id}").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(jedi.id))
            assertTrue(bodyAsText().contains("LIGHT"))
            assertFalse(bodyAsText().contains(sith.id))
            assertFalse(bodyAsText().contains("DARK"))
        }
    }

    @Test
    fun testDroidModule() = testApplication {
        application {
            commonModule()
            droidModuleWith(object : DroidContext {
                override val droidRepository = LiveDroidRepository(IntegrationTestLayer)
                override val logger = IntegrationTestLayer.logger
            })
        }

        testCrud(
            "/droids",
            Droid(designation = "4-LOM", type = Droid.Type.Protocol),
            Droid(designation = "R5-D4", type = Droid.Type.Astromech)
        )
    }

    private suspend fun <T : Data> ApplicationTestBuilder.testCrud(resource: String, item1: T, item2: T) {
        client.post(resource) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(item1.toJson())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(item1.id))
            assertFalse(bodyAsText().contains(item2.id))
        }

        client.post(resource) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(item2.toJson())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(item2.id))
            assertFalse(bodyAsText().contains(item1.id))
        }

        client.post(resource) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(item2.toJson())
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status)
            assertEquals("", bodyAsText())
        }

        client.get(resource).apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(item1.id))
            assertTrue(bodyAsText().contains(item2.id))
        }

        client.get("${resource}/${item1.id}").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(item1.id))
            assertFalse(bodyAsText().contains(item2.id))
        }

        client.get("${resource}/${UUID.randomUUID()}").apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("", bodyAsText())
        }

        client.delete("${resource}/${item2.id}").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(item2.id))
            assertFalse(bodyAsText().contains(item1.id))
        }

        client.delete("${resource}/${item2.id}").apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("", bodyAsText())
        }
        return
    }

    private fun Any.toJson() = jacksonObjectMapper().writeValueAsString(this)

}
