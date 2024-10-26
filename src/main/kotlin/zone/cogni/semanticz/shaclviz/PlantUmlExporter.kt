package zone.cogni.semanticz.shaclviz

import zone.cogni.semanticz.shaclviz.Graph.Companion.maxCount
import zone.cogni.semanticz.shaclviz.Graph.Companion.minCount
import java.io.File
import java.nio.file.Files

/**
 * Export to PlantUml
 */
class PlantUmlExporter : Exporter {

    private fun renderField(field: Map.Entry<Property, Class.ClassWithCardinality>): String {
        return "- ${field.key.name}: ${field.value.cls.name} ${countInfo(field.value.min, field.value.max)}"
    }

    private fun countInfo(minCount: Int, maxCount: String) =
        if (minCount != 0 || maxCount != "*") "[${minCount},${maxCount}]" else ""

    private fun render(index: String, cls: Class) =
        "class \"${cls.name}\" as c$index {\n" +
                (if (cls.fields.isNotEmpty()) {
                    cls.fields.toSortedMap(compareBy { it.name }).map { renderField(it) }.joinToString("\n")
                } else "") + "\n" +
                "}\n\n"

    override fun export(graph: Graph, fileName: String) {
        val result = "@startuml \nskinparam linetype polyline\n\n" + graph.classes.mapIndexed { index, c ->
            render(graph.classIndex(c.iri), c)
        }.joinToString(separator = "\n") + "\n" + "\n" +
                graph.edges.joinToString(separator = "\n") { e: Constraint ->
                    val propertyName =
                        "${e.propertyName} ${countInfo(minCount(e)!!, maxCount(e)!!)}"
                    "c${graph.classIndex(e.classIri)} --> c${graph.classIndex(e.rangeIri?:"")} : \"$propertyName\""
                } + "\n" +
                "@enduml"
        Files.writeString(File(fileName).toPath(), result)
    }
}
