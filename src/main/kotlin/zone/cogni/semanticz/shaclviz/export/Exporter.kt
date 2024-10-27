package zone.cogni.semanticz.shaclviz.export

import zone.cogni.semanticz.shaclviz.model.Graph
import java.io.Writer

/**
 * Representation of a data model class.
 */
interface Exporter {

    companion object {
        fun get(name: String): Exporter =
            when (name) {
                "tgf" -> TgfExporter()
                "puml" -> PlantUmlExporter()
                else -> throw IllegalArgumentException("Unknown exporter: $name")
            }
    }

    fun export(graph: Graph, writer: Writer)

}