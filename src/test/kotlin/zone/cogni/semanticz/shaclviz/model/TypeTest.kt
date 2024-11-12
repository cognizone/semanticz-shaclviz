package zone.cogni.semanticz.shaclviz.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TypeTest {

    @Test
    fun testGetHtmlWithoutField() {
        val c = Type("http://test.org/test", "test")
        Assertions.assertEquals("test", c.name)
        Assertions.assertEquals("http://test.org/test", c.iri)
        Assertions.assertEquals(0, c.fields.size)
    }

    @Test
    fun testGetHtmlWithField() {
        val c = Type("http://test.org/test", "test")
        c.addField(Property("http://test.org/testp", "p"), Type("http://test.org/test", "r"), 1, "")
        Assertions.assertEquals(1, c.fields.size)
        val (property, typeAsRange) = c.fields.iterator().next()
        Assertions.assertEquals("http://test.org/testp", property.iri)
        Assertions.assertEquals("p", property.name)
        Assertions.assertEquals("http://test.org/test", typeAsRange.cls.iri)
        Assertions.assertEquals("r", typeAsRange.cls.name)
        Assertions.assertEquals(1, typeAsRange.min)
        Assertions.assertEquals("", typeAsRange.max)

    }
}