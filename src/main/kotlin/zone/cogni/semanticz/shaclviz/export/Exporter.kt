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

    /**
     * Exports a graph to the writer.
     */
    fun export(graph: Graph, writer: Writer, hideOrphanNodes: Boolean)

}