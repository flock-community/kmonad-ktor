package common

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class GenerateSwaggerFile {

    @Test
    fun generateSwaggerJson() = setup {
        handleRequest(HttpMethod.Get, "/spec/swagger.json").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            File("build/swagger.json").writeText(response.content!!)
        }
    }

}
