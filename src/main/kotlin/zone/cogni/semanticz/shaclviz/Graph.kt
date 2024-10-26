package zone.cogni.semanticz.shaclviz

import org.apache.jena.query.QueryExecution
import org.apache.jena.query.QuerySolution
import java.io.File
import java.nio.file.Files

/**
 * Representation of a graph.
 */
class Graph {

    private val classes: MutableList<Class> = ArrayList()
    private val edges: MutableList<QuerySolution> = ArrayList()

    fun parse(
        qe: QueryExecution,
        fieldClasses: Set<String>,
        filter: (s: String, p: String?, o: String?, req: Boolean) -> Boolean
    ) {
        qe.execSelect().forEachRemaining { s: QuerySolution ->
            val clsName = s["className"].toString()
            val prpName = s["propertyName"]?.toString()
            val rngName = s["rangeName"]?.toString()
            val clsIri = s["class"].toString()
            val prpIri = s["property"]?.toString()
            val rngIri = s["range"]?.toString()

            val required = required(s)

            if (!filter(clsIri, prpIri, rngIri, required)) {
                return@forEachRemaining
            }

            if (prpIri != null && rngIri != null) {
                if (classes.all { it.iri != clsIri }) classes.add(Class(clsIri, clsName))
                val cc = classes.first { it.iri == clsIri }
                if (classes.all { it.iri != rngIri }) classes.add(Class(rngIri, rngName))
                val rng = classes.first { it.iri == rngIri }
                if (fieldClasses.contains(rngIri)) {
                    cc.addField(Property(prpIri, prpName), rng, countInfo(minCount(s)!!, maxCount(s)!!.string))
                } else {
                    edges.add(s)
                }
            }
        }
    }

    private fun classIndex(e: QuerySolution, n: String) =
        (classes.indexOfFirst { it.iri == e[n].toString() } + 1).toString()

    private fun required(e: QuerySolution) = (minCount(e) ?: 0) > 0

    private fun minCount(e: QuerySolution) = e.get("minCount")?.asLiteral()?.int

    private fun maxCount(e: QuerySolution) = e.get("maxCount")?.asLiteral()

    private fun countInfo(minCount : Int, maxCount: String) = if (minCount != 0 || maxCount != "*") "[${minCount},${maxCount}]" else ""

    fun exportToTgf(fileName: String) {
        val result = classes.mapIndexed { index, c ->
            "${index + 1} ${c.getHTMLLabel()}"
        }.joinToString(separator = "\n") + "\n" + "#\n" +
                edges.joinToString(separator = "\n") { e: QuerySolution ->
                    val propertyName = "${e["propertyName"].asLiteral()} ${countInfo(minCount(e)!!,maxCount(e)!!.string)}"
                    "${classIndex(e, "class")} ${classIndex(e, "range")} <html><body>$propertyName</body></html>"
                }
        Files.writeString(File(fileName).toPath(), result)
    }
}