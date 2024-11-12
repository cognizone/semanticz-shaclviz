package zone.cogni.semanticz.shaclviz.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.*
import kotlin.io.path.deleteExisting
import kotlin.io.path.writeText

class UtilsTest {

    @Test
    fun loadResourceContentLoadsCorrectlyExistingResource() {
        val content = Utils.loadResourceContent("classpath:/example.ttl")
        Assertions.assertNotNull(content)
        Assertions.assertTrue(content?.length!! > 0)
    }

    @Test
    fun loadResourceContentFailsForNonExistingResource() {
        val content = Utils.loadResourceContent("classpath:/" + UUID.randomUUID())
        Assertions.assertNull(content)
    }

    @Test
    fun loadResourceContentLoadsCorrectlyExistingFile() {
        val f = Files.createTempFile("test", ".txt");
        f.writeText("hello")
        val content = Utils.loadResourceContent(f.toFile().absolutePath)
        Assertions.assertEquals("hello",content)
        f.deleteExisting()
    }

    @Test
    fun loadResourceContentFailsForNonExistingFile() {
        val content = Utils.loadResourceContent("" + UUID.randomUUID())
        Assertions.assertNull(content)
    }
}