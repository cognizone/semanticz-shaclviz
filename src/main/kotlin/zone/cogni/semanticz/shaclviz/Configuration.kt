package zone.cogni.semanticz.shaclviz

const val SKOS = "http://www.w3.org/2004/02/skos/core#"
const val LOCN = "http://www.w3.org/ns/locn#"
const val ELM = "http://data.europa.eu/snb/model/elm/"
const val XSD = "http://www.w3.org/2001/XMLSchema#"
const val RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
const val FOAF = "http://xmlns.com/foaf/0.1/"
const val DC = "http://purl.org/dc/terms/"

val datatypes = setOf(
    XSD + "string",
    XSD + "int",
    XSD + "integer",
    XSD + "positiveInteger",
    XSD + "dateTime",
    RDF + "langString",
    XSD + "anyURI",
    XSD + "boolean",
    XSD + "duration",
    XSD + "decimal",
    RDF + "HTML"
)

open class Configuration(
    val fieldClasses: Set<String> = datatypes,
    val filter: (s: String?, p: String?, o: String?, req: Boolean) -> Boolean = { _, _, _, _ -> true }
)
