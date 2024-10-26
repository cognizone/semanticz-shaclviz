package zone.cogni.semanticz.shaclviz

/**
 * Representation of a data model class.
 */
interface Exporter {

    fun export(graph: Graph, fileName: String)
}