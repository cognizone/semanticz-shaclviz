package zone.cogni.semanticz.shaclviz

import zone.cogni.semanticz.shaclviz.Graph.Companion.maxCount
import zone.cogni.semanticz.shaclviz.Graph.Companion.minCount
import java.io.File
import java.nio.file.Files

/**
 * Representation of a data model class.
 */
class TgfExporter : Exporter {

    private fun renderField(field: Map.Entry<Property, Class.ClassWithCardinality>): String {
        return "<li>${field.key.name}: ${field.value.cls.name} ${countInfo(field.value.min, field.value.max)}</li>"
    }

    private fun countInfo(minCount: Int, maxCount: String) =
        if (minCount != 0 || maxCount != "*") "[${minCount},${maxCount}]" else ""

    private fun render(cls: Class) =
        "<html><body><h3>${cls.name}</h3>${
            if (cls.fields.isNotEmpty()) {
                "<ul>${cls.fields.toSortedMap(compareBy { it.name }).map { renderField(it) }.joinToString("")}</ul>"
            } else ""
        }</body></html>"

    override fun export(graph: Graph, fileName: String) {
        val result = graph.classes.mapIndexed { index, c ->
            "${index + 1} ${render(c)}"
        }.joinToString(separator = "\n") + "\n" + "#\n" +
                graph.edges.joinToString(separator = "\n") { e: Constraint ->
                    val propertyName =
                        "${e.propertyName} ${countInfo(minCount(e)!!, maxCount(e)!!)}"
                    "${graph.classIndex(e.classIri)} ${graph.classIndex(e.rangeIri?:"")} <html><body>$propertyName</body></html>"
                }
        Files.writeString(File(fileName).toPath(), result)
    }
}