package zone.cogni.semanticz.shaclviz

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import org.apache.jena.query.QueryFactory
import java.io.File

class Generate : CliktCommand() {
    private val modelFile by argument(name = "--modelFile", help = "The SHACL model file to process.")
    private val outputFile by argument(name = "--outputFile", help = "The output file to write the diagram to.")
    private val graphQuery by option(
        "--graphQuery",
        help = "Optional Definition of a query to generate the graph constraints."
    )
        .default("classpath:/shacl-datamodel-view.rq")
    private val filterQuery by option(
        "--filterQuery",
        help = "Optional Definition of a query to filter constraints."
    ).default("classpath:/edges-all.rq")
    private val fieldQuery by option(
        "--fieldQuery",
        help = "Selects additional IRIs to be displayed as fields."
    ).default("classpath:/fields-datatypes.rq")

    override fun help(context: Context) = "Generates a diagram"
    override fun run() {
        val graphQuery = QueryFactory.create(loadResourceContent(graphQuery))
        val filterQuery = QueryFactory.create(loadResourceContent(filterQuery))
        val fieldQuery = QueryFactory.create(loadResourceContent(fieldQuery))
        DiagramGenerator(modelFile, outputFile, graphQuery, filterQuery, fieldQuery).generate()
    }
}

fun loadResourceContent(path: String): String? {
    return if (path.startsWith("classpath:")) {
        val resourcePath = path.removePrefix("classpath:")
        object {}.javaClass.getResourceAsStream(resourcePath)?.bufferedReader()?.use { it.readText() }
    } else {
        val file = File(path)
        if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }
}

fun main(args: Array<String>) = Generate().main(args)