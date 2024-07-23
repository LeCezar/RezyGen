import com.lecezar.rezygen.mainModule
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import models.TestResponse
import org.junit.Test
import kotlin.test.assertEquals

class ServerTest {
    @Test
    fun testRoot() = testApplication {
        application {
            mainModule()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            val result: TestResponse = Json.decodeFromString(bodyAsText())
            assertEquals(TestResponse::class.simpleName, result::class.simpleName)
        }
    }
}