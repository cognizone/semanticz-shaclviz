package zone.cogni.semanticz.shaclviz

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ClassTest {

    @Test
    fun testGetHtmlWithoutField() {
        val c = Class("http://test.org/test", "test")
        Assertions.assertEquals("<html><body><h3>test</h3></body></html>", c.getHTMLLabel())
    }

    @Test
    fun testGetHtmlWithField() {
        val c = Class("http://test.org/test", "test")
        c.addField(Property("http://test.org/testp","p"), Class("http://test.org/test","r"), "")
        Assertions.assertEquals("<html><body><h3>test</h3><ul><li>p: r </li></ul></body></html>", c.getHTMLLabel())
    }
}