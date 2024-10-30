package zone.cogni.semanticz.shaclviz.export

import zone.cogni.semanticz.shaclviz.model.*
import java.io.Writer

/**
 * Representation of a data model class.
 */
class TgfExporter : Exporter {

    private fun renderField(field: Map.Entry<Property, TypeAsRange>): String {
        return "<li>${field.key.name}: ${field.value.cls.name} ${countInfo(field.value.min, field.value.max)}</li>"
    }

    private fun countInfo(minCount: Int, maxCount: String) =
        if (minCount != 0 || maxCount != "*") "[${minCount},${maxCount}]" else ""

    private fun render(cls: Type) =
        "<html><body><h3>${cls.name}</h3>${
            if (cls.fields.isNotEmpty()) {
                "<ul>${cls.fields.toSortedMap(compareBy { it.name }).map { renderField(it) }.joinToString("")}</ul>"
            } else ""
        }</body></html>"

    override fun export(graph: Graph, writer: Writer) {
        val result = graph.nodes.mapIndexed { index, c ->
            "${index + 1} ${render(c)}"
        }.joinToString(separator = "\n") + "\n" + "#\n" +
                graph.edges.joinToString(separator = "\n") { e: Constraint ->
                    val propertyName =
                        "${e.propertyName} ${countInfo(e.minCount!!, e.maxCount!!)}"
                    "${graph.classIndex(e.classIri)} ${graph.classIndex(e.rangeIri?:"")} <html><body>$propertyName</body></html>"
                }
        writer.write(result)
    }
}