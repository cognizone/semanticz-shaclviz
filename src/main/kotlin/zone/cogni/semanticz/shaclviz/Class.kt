package zone.cogni.semanticz.shaclviz

import kotlin.math.min

/**
 * Representation of a data model class.
 */
class Class(
    iri: String,
    name: String?
) : Resource(iri, name) {

    private val fields: MutableMap<Property, Class> = mutableMapOf()
    private val countInfos: MutableMap<Property, String> = mutableMapOf()

    fun addField(property: Property, range: Class, countInfo: String) {
        fields[property] = range
        countInfos[property] = countInfo
    }

    private fun renderField(field: Map.Entry<Property, Class>) : String  {
        return "<li>${field.key.name}: ${field.value.name} ${countInfos[field.key]}</li>"
    }

    fun getHTMLLabel() =
        "<html><body><h3>$name</h3>${
            if (fields.isNotEmpty()) {
                "<ul>${fields.toSortedMap(compareBy { it.name }).map { renderField(it) }.joinToString("")}</ul>"
            } else ""
        }</body></html>"
}