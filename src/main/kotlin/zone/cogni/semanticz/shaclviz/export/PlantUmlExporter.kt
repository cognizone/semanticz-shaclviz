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

package zone.cogni.semanticz.shaclviz.export

import zone.cogni.semanticz.shaclviz.model.*
import java.io.Writer

/**
 * Export to PlantUml
 */
class PlantUmlExporter : Exporter {

    private fun renderField(field: Map.Entry<Property, TypeAsRange>): String {
        return "- ${field.key.name}: ${field.value.cls.name} ${countInfo(field.value.min, field.value.max)}"
    }

    private fun countInfo(minCount: Int, maxCount: String) =
        if (minCount != 0 || maxCount != "*") "[${minCount},${maxCount}]" else ""

    private fun render(index: String, cls: Type) =
        "class \"${cls.name}\" as c$index {\n" +
                (if (cls.fields.isNotEmpty()) {
                    cls.fields.toSortedMap(compareBy { it.name }).map { renderField(it) }.joinToString("\n")
                } else "") + "\n" +
                "}\n\n"

    override fun export(graph: Graph, writer: Writer, hideOrphanNodes: Boolean) {
        val result = "@startuml \nskinparam linetype polyline\n\n" + graph.nodes
            .filter { n -> !hideOrphanNodes || n.fields.isNotEmpty() || graph.ins(n).isNotEmpty() || graph.outs(n).isNotEmpty() }
            .mapIndexed { _, c ->
            render(graph.classIndex(c.iri), c)
        }.joinToString(separator = "\n") + "\n" + "\n" +
                graph.edges.joinToString(separator = "\n") { e: Constraint ->
                    val propertyName =
                        "${e.propertyName} ${countInfo(e.minCount!!, e.maxCount!!)}"
                    "c${graph.classIndex(e.classIri)} --> c${graph.classIndex(e.rangeIri?:"")} : \"$propertyName\""
                } + "\n" +
                "@enduml"
        writer.write(result)
    }
}
