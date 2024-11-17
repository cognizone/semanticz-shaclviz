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

package zone.cogni.semanticz.shaclviz.model

import org.apache.jena.query.*
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import zone.cogni.semanticz.shaclviz.util.JenaUtils.resultSetToValuesBlock
import zone.cogni.semanticz.shaclviz.util.JenaUtils.setValuesBlockToQueryPattern

/**
 * Representation of a graph structure.
 *
 * @property nodes List of nodes in the graph.
 * @property edges List of graph edges.
 */
class Graph {

    /**
     * List of types in the graph. Constraints interpreted as fields are part of each type.
     */
    val nodes: MutableList<Type> = ArrayList()

    /**
     * List of constraints in the graph interpreted as edges.
     */
    var edges: MutableList<Constraint> = ArrayList()

    companion object {

        private val vars = mutableListOf(
            "class",
            "className",
            "property",
            "propertyName",
            "range",
            "rangeName",
            "minCount",
            "maxCount"
        )

        private fun parseConstraints(model: Model, graphQuery: Query, filterQuery: Query): List<Constraint> =
            mutableListOf<Constraint>().also {
                filterQuery.setValuesBlockToQueryPattern(
                    QueryExecution.create(graphQuery, model).execSelect().resultSetToValuesBlock()
                )
                QueryExecution.create(filterQuery, ModelFactory.createDefaultModel()).execSelect()
                    .forEachRemaining { s: QuerySolution ->
                        it.add(
                            Constraint(
                                s[vars[0]].toString(),
                                s[vars[1]]?.toString(),
                                s[vars[2]]?.toString(),
                                s[vars[3]]?.toString(),
                                s[vars[4]]?.toString(),
                                s[vars[5]]?.toString(),
                                s[vars[6]]?.asLiteral()?.lexicalForm?.toInt(),
                                s[vars[7]]?.asLiteral()?.lexicalForm,
                            )
                        )
                    }
            }
    }

    /**
     * Parses the graph structure given the
     * - graphQuery - defines the SHACL structure extraction
     * - filterQuery - filters the constraints to be used
     * - fieldQuery - defines which types will be considered fields (instead of nodes)
     */
    fun parse(
        model: Model,
        graphQuery: Query,
        filterQuery: Query,
        fieldQuery: Query,
    ) {
        val constraints: List<Constraint> = parseConstraints(model, graphQuery, filterQuery)
        val fieldClasses = mutableListOf<String>()

        QueryExecution.create(fieldQuery, model).execSelect().forEachRemaining { s: QuerySolution ->
            fieldClasses.add(s["field"].toString())
        }

        constraints.forEach { edge ->
            if (edge.propertyIri != null && edge.rangeIri != null) {
                if (nodes.all { it.iri != edge.classIri }) nodes.add(Type(edge.classIri, edge.className))
                val cc = nodes.first { it.iri == edge.classIri }
                if (nodes.all { it.iri != edge.rangeIri }) nodes.add(Type(edge.rangeIri, edge.rangeName))
                val rng = nodes.first { it.iri == edge.rangeIri }
                if (fieldClasses.contains(edge.rangeIri)) {
                    cc.addField(
                        Property(edge.propertyIri, edge.propertyName),
                        rng,
                        edge.minCount!!,
                        edge.maxCount!!
                    )
                } else {
                    edges.add(edge)
                }
            }
        }
    }

    fun classIndex(n: String) =
        (nodes.indexOfFirst { it.iri == n } + 1).toString()

    fun ins(n: Type) =
        edges.filter { e -> e.classIri == n.iri }

    fun outs(n: Type) =
        edges.filter { e -> e.rangeIri == n.iri }

}