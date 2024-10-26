package zone.cogni.semanticz.shaclviz

import org.apache.jena.query.Query
import org.apache.jena.rdf.model.ModelFactory

class DiagramGenerator(
    private val modelFile: String,
    private val outputFile: String,
    private val graphQuery: Query,
    private val filterQuery: Query,
    private val fieldQuery: Query
) {

    fun generate() {
        val model = ModelFactory.createDefaultModel().read(modelFile)
        val g = Graph()
        g.parse(model, graphQuery, filterQuery, fieldQuery)
        println("Writing output to $outputFile")
        PlantUmlExporter().export(g, outputFile)
    }
}