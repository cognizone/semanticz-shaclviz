package zone.cogni.semanticz.shaclviz.model

import org.apache.jena.query.*
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.engine.binding.Binding
import org.apache.jena.sparql.syntax.ElementData
import org.apache.jena.sparql.syntax.ElementGroup

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

            println(filterQuery.toString(Syntax.syntaxSPARQL))

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
                            s[vars[6]]?.asLiteral()?.lexicalForm,
                            s[vars[7]]?.asLiteral()?.lexicalForm,
                        )
                    )
                }
            println(edges.size)
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