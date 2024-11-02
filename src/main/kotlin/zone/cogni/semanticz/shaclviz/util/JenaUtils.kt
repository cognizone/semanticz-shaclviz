package zone.cogni.semanticz.shaclviz.util

import org.apache.jena.query.Query
import org.apache.jena.query.ResultSet
import org.apache.jena.sparql.engine.binding.Binding
import org.apache.jena.sparql.syntax.ElementData
import org.apache.jena.sparql.syntax.ElementGroup

/**
 * Extension functions for Jena
 */
object JenaUtils {

    /**
     * Converts a ResultSet to a list of Bindings.
     *
     * @receiver The ResultSet to convert.
     * @return The list of Bindings.
     */
    fun ResultSet.resultSetToValuesBlock(): List<Binding> =
        mutableListOf<Binding>().apply {
            while (this@resultSetToValuesBlock.hasNext()) {
                this.add(this@resultSetToValuesBlock.nextBinding())
            }
        }

    /**
     * Sets a list of Bindings as a values block inside a Query.
     *
     * This method does not set the VALUES block for the whole query, but for the query pattern instead.
     *
     * @receiver query The query to set the values block to.
     * @param valuesBlock The values block to set.
     * @return The query with the values block set.
     */
    fun Query.setValuesBlockToQueryPattern(valuesBlock: List<Binding>) {
        if (valuesBlock.isEmpty()) {
            return
        }
        this.queryPattern = ElementGroup().also { eg ->
            eg.addElement(ElementData().apply {
                valuesBlock[0].vars().forEach { this.add(it) }
                valuesBlock.forEach { this.add(it) }
            })
            (this.queryPattern as ElementGroup).elements.forEach { el -> eg.addElement(el) }
        }
    }
}