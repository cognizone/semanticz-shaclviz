package zone.cogni.semanticz.shaclviz

import org.apache.jena.query.QueryExecution
import org.apache.jena.rdf.model.ModelFactory
import java.lang.Class
import kotlin.reflect.full.createInstance

fun main(args: Array<String>) {
    val modelFile = args[0] // "/home/psiotwo/soft/cognizone/acqf-qpc/data-model/acqf.ttl";
    val outputFile = args[1] // "/home/psiotwo/soft/cognizone/acqf-qpc/data-model/acqf.ttl";
    val configuration = if (args.size > 2) {  // "zone.cogni.shacl.diagram.acqf.configurations.OverviewConfiguration";
        Class.forName(args[2]).kotlin.createInstance() as Configuration
    } else {
        Configuration()
    }

    val content = Thread.currentThread().contextClassLoader.getResource("generate-diagram.rq")?.readText()
    val model = ModelFactory.createDefaultModel().read(modelFile)
    QueryExecution.create(content, model).use {
        val g = Graph()
        g.parse(it, configuration.fieldClasses, configuration.filter)
        println("Writing output to $outputFile")
        g.exportToTgf(outputFile)
    }
}