package zone.cogni.semanticz.shaclviz

import org.apache.jena.query.QueryExecution
import org.apache.jena.query.QueryFactory
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.engine.binding.BindingFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import zone.cogni.semanticz.shaclviz.util.JenaUtils.resultSetToValuesBlock
import zone.cogni.semanticz.shaclviz.util.JenaUtils.setValuesBlockToQueryPattern

class JenaUtilsTest {

    @Test
    fun testResultSetToValuesBlock() {
        val rs = QueryExecution.create(
            "SELECT ?x {} VALUES ?x { <https://a> <https://b> }",
            ModelFactory.createDefaultModel()
        ).execSelect()
        val bindings = rs.resultSetToValuesBlock()
        Assertions.assertEquals(2, bindings.size)
        Assertions.assertEquals("https://a", bindings[0]["x"].toString())
        Assertions.assertEquals("https://b", bindings[1]["x"].toString())
    }

    @Test
    fun testSetValuesBlockToQueryPattern() {
        val q = QueryFactory.create("SELECT * { }")
        val bindings =
            listOf(BindingFactory.binding(
                Var.alloc("s"), ResourceFactory.createResource("https://a").asNode(),
                Var.alloc("p"), ResourceFactory.createResource("https://b").asNode(),
                Var.alloc("o"), ResourceFactory.createResource("https://c").asNode()
            ))
        q.setValuesBlockToQueryPattern(bindings)
        Assertions.assertFalse(q.hasValues())

        println(q.toString())

        val rs = QueryExecution.create(q, ModelFactory.createDefaultModel()).execSelect()
        Assertions.assertTrue(rs.hasNext())
        val b = rs.nextBinding()
        Assertions.assertEquals("https://a", b["s"].toString())
        Assertions.assertEquals("https://b", b["p"].toString())
        Assertions.assertEquals("https://c", b["o"].toString())
        Assertions.assertFalse(rs.hasNext())
    }
}