/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package zone.cogni.semanticz.shaclviz

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import org.apache.jena.query.QueryFactory
import org.apache.jena.rdf.model.ModelFactory
import zone.cogni.semanticz.shaclviz.export.Exporter
import zone.cogni.semanticz.shaclviz.model.Graph
import zone.cogni.semanticz.shaclviz.util.Utils.loadResourceContent
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
    private val outputFormat by option(
        "--outputFormat",
        help = """Graph format:
            - 'puml' - PlantUML diagram format. Suitable for quick visualization.
            - 'tgf' - Trivial Graph Format importable by yEd. Suitable for full control over layout and further postprocessing.""".trimMargin()
    ).default("puml")

    private val hideOrphanNodes by option(
        "--hideOrphanNodes",
        help = """In case a node has not inbound/outbound connection and no fields, it is hidden.""".trimMargin()
    ).default("true")

    override fun help(context: Context) = "Generates a diagram"
    override fun run() {
        val model = ModelFactory.createDefaultModel().read(modelFile)
        val graphQuery = QueryFactory.create(loadResourceContent(graphQuery))
        val filterQuery = QueryFactory.create(loadResourceContent(filterQuery))
        val fieldQuery = QueryFactory.create(loadResourceContent(fieldQuery))
        val writer = File(outputFile).bufferedWriter()
        val exporter = Exporter.get(outputFormat)
        Graph().apply {
            parse(model, graphQuery, filterQuery, fieldQuery)
            exporter.export(this, writer, hideOrphanNodes.toBoolean())
        }
        writer.close()
    }
}

fun main(args: Array<String>) = Generate().main(args)