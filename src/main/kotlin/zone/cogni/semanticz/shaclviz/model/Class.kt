package zone.cogni.semanticz.shaclviz.model

/**
 * Representation of a data model class.
 */
class Class(
    iri: String,
    name: String?
) : Resource(iri, name) {

    val fields: MutableMap<Property, ClassWithCardinality> = mutableMapOf()

    fun addField(property: Property, range: Class, minCount: Int, maxCount: String) {
        fields[property] = ClassWithCardinality(range, minCount, maxCount)
    }

    class ClassWithCardinality(val cls: Class, val min: Int, val max: String)
}