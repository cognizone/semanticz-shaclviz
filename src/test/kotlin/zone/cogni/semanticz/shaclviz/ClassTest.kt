package zone.cogni.semanticz.shaclviz

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ClassTest {

    @Test
    fun testGetHtmlWithoutField() {
        val c = Class("http://test.org/test", "test")
        Assertions.assertEquals("test", c.name)
        Assertions.assertEquals("http://test.org/test", c.iri)
        Assertions.assertEquals(0, c.fields.size)
    }

    @Test
    fun testGetHtmlWithField() {
        val c = Class("http://test.org/test", "test")
        c.addField(Property("http://test.org/testp", "p"), Class("http://test.org/test", "r"), 1, "")
        Assertions.assertEquals(1, c.fields.size)
        val (property, classWithCardinality) = c.fields.iterator().next()
        Assertions.assertEquals("http://test.org/testp", property.iri)
        Assertions.assertEquals("p", property.name)
        Assertions.assertEquals("http://test.org/test", classWithCardinality.cls.iri)
        Assertions.assertEquals("r", classWithCardinality.cls.name)
        Assertions.assertEquals(1, classWithCardinality.min)
        Assertions.assertEquals("", classWithCardinality.max)

    }
}