package zone.cogni.semanticz.shaclviz.model

import org.apache.jena.query.*
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.engine.binding.Binding
import org.apache.jena.sparql.syntax.ElementData
import org.apache.jena.sparql.syntax.ElementGroup

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

        private fun resultSetToValuesBlock(resultSet: ResultSet): List<Binding> =
            mutableListOf<Binding>().apply {
                while (resultSet.hasNext()) {
                    this.add(resultSet.nextBinding())
                }
            }

        private fun setValuesBlockToQueryPattern(query: Query, valuesBlock: List<Binding>) {
            val valuesBlockNew = ElementData()
            vars.map { varName: String ->
                valuesBlockNew.add(Var.alloc(varName))
            }
            valuesBlock.forEach { valuesBlockNew.add(it) }
            ElementGroup().also {
                it.addElement(valuesBlockNew)

                val originalPattern = query.queryPattern as ElementGroup
                for (el in originalPattern.elements) {
                    it.addElement(el)
                }
                query.queryPattern = it
            }
        }

        private fun parseConstraints(model: Model, graphQuery: Query, filterQuery: Query): List<Constraint> {
            val edges = mutableListOf<Constraint>()
            val valuesBlock = resultSetToValuesBlock(QueryExecution.create(graphQuery, model).execSelect())
            setValuesBlockToQueryPattern(filterQuery, valuesBlock)

            QueryExecution.create(filterQuery, ModelFactory.createDefaultModel()).execSelect()
                .forEachRemaining { s: QuerySolution ->
                    edges.add(
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
            return edges
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
            fieldClasses.add(s.get("field").toString())
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
}