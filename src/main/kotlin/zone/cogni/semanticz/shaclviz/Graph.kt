package zone.cogni.semanticz.shaclviz

import org.apache.jena.query.Query
import org.apache.jena.query.QueryExecution
import org.apache.jena.query.QuerySolution
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.engine.binding.Binding

/**
 * Representation of a graph.
 */
class Graph {

    val classes: MutableList<Class> = ArrayList()
    var edges: MutableList<Constraint> = ArrayList()

    companion object {
        private fun resultSetToValuesBlock(resultSet: ResultSet): List<Binding> {
            val bindings = mutableListOf<Binding>()
            while (resultSet.hasNext()) {
                bindings.add(resultSet.nextBinding())
            }
            return bindings
        }

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

        fun minCount(e: Constraint) = e.minCount?.toInt()

        fun maxCount(e: Constraint) = e.maxCount

        private fun parseConstraints(model: Model, graphQuery: Query, filterQuery: Query): List<Constraint> {
            val edges = mutableListOf<Constraint>()
            val valuesBlock = resultSetToValuesBlock(QueryExecution.create(graphQuery, model).execSelect())
            filterQuery.setValuesDataBlock(vars.map { varName: String -> Var.alloc(varName) }, valuesBlock)
            QueryExecution.create(graphQuery, model).execSelect().forEachRemaining { s: QuerySolution ->
                println(s)
                edges.add(
                    Constraint(
                        s[vars[0]].toString(),
                        s[vars[1]]?.toString(),
                        s[vars[2]]?.toString(),
                        s[vars[3]]?.toString(),
                        s[vars[4]]?.toString(),
                        s[vars[5]]?.toString(),
                        s[vars[6]]?.asLiteral()?.lexicalForm,
                        s[vars[7]]?.asLiteral()?.lexicalForm,
                    )
                )
            }
            return edges
        }
    }

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
                if (classes.all { it.iri != edge.classIri }) classes.add(Class(edge.classIri, edge.className))
                val cc = classes.first { it.iri == edge.classIri }
                if (classes.all { it.iri != edge.rangeIri }) classes.add(Class(edge.rangeIri, edge.rangeName))
                val rng = classes.first { it.iri == edge.rangeIri }
                if (fieldClasses.contains(edge.rangeIri)) {
                    cc.addField(
                        Property(edge.propertyIri, edge.propertyName),
                        rng,
                        minCount(edge)!!,
                        maxCount(edge)!!
                    )
                } else {
                    edges.add(edge)
                }
            }
        }
    }

    fun classIndex(n: String) =
        (classes.indexOfFirst { it.iri == n } + 1).toString()
}