package zone.cogni.semanticz.shaclviz

import org.apache.jena.query.Query
import org.apache.jena.rdf.model.ModelFactory
import zone.cogni.semanticz.shaclviz.export.Exporter
import zone.cogni.semanticz.shaclviz.model.Graph
import java.io.Writer

class DiagramGenerator(
    private val modelFile: String,
    private val graphQuery: Query,
    private val filterQuery: Query,
    private val fieldQuery: Query,
    private val exporter: Exporter,
    private val writer: Writer,
) {

    fun generate() {
        Graph().apply {
            parse(ModelFactory.createDefaultModel().read(modelFile), graphQuery, filterQuery, fieldQuery)
            exporter.export(this, writer)
        }
    }
}