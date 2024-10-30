package zone.cogni.semanticz.shaclviz.model

/**
 * Representation of a data model type (Class/Datatype).
 */
class Type(
    iri: String,
    name: String?
) : Resource(iri, name) {

    val fields: MutableMap<Property, TypeAsRange> = mutableMapOf()

    fun addField(property: Property, range: Type, minCount: Int, maxCount: String) {
        fields[property] = TypeAsRange(range, minCount, maxCount)
    }
}