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
 * Representation of a data model class.
 */
class TgfExporter : Exporter {

    private fun renderField(field: Map.Entry<Property, TypeAsRange>): String {
        return "<li>${field.key.name}: ${field.value.cls.name} ${countInfo(field.value.min, field.value.max)}</li>"
    }

    private fun countInfo(minCount: Int, maxCount: String) =
        if (minCount != 0 || maxCount != "*") "[${minCount},${maxCount}]" else ""

    private fun render(cls: Type) =
        "<html><body><h3>${cls.name}</h3>${
            if (cls.fields.isNotEmpty()) {
                "<ul>${cls.fields.toSortedMap(compareBy { it.name }).map { renderField(it) }.joinToString("")}</ul>"
            } else ""
        }</body></html>"

    override fun export(graph: Graph, writer: Writer, hideOrphanNodes: Boolean) {
        val result = graph.nodes
            .filter { n -> !hideOrphanNodes || n.fields.isNotEmpty() || graph.ins(n).isNotEmpty() || graph.outs(n).isNotEmpty() }
            .mapIndexed { _, c ->
            "${graph.classIndex(c.iri)} ${render(c)}"
        }.joinToString(separator = "\n") + "\n" + "#\n" +
                graph.edges.joinToString(separator = "\n") { e: Constraint ->
                    val propertyName =
                        "${e.propertyName} ${countInfo(e.minCount!!, e.maxCount!!)}"
                    "${graph.classIndex(e.classIri)} ${graph.classIndex(e.rangeIri?:"")} <html><body>$propertyName</body></html>"
                }
        writer.write(result)
    }
}