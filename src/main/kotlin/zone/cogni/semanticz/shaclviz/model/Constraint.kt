package zone.cogni.semanticz.shaclviz.model

/**
 * Representation of a constraint.
 */
class Constraint(
    val classIri: String,
    val className: String?,
    val propertyIri: String?,
    val propertyName: String?,
    val rangeIri: String?,
    val rangeName: String?,
    val minCount: String?,
    val maxCount: String?,
)