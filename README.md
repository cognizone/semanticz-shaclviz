# SHACL Viz

This tool allows you to visualize SHACL data model files as a UML-like class diagram with the following features:
- diagram nodes correspond to the target classes of `sh:targetClass` of `sh:NodeShape`, not the `sh:NodeShape` themselves.
- class labels are taken from `dc:title` of the node shapes (falling back to `rdfs:label` of the `sh:targetClass`)
- diagram edges correspond to `sh:PropertyShape`s
- their labels are taken from `sh:name` (falling back to `sh:description`)
- configurable RDFS classes/datatypes that will be visualized as fields (not edges) in the diagram.
- configurable filter to ignore certain domainClass-property-rangeClass combinations (e.g. to select only required elements for the diagram)
- export formats - PlantUML (auto-layout), TGF graph importable by yEd (for advanced layouts and manual editing)

## Diagram Generation

The diagram generation works specific form of a SHACL file representing a _data model_ with the following constraints:
- a node shape to be visualized must have
  - `sh:targetClass` pointing to an IRI of a class
- each property shape
  - must be an IRI, not a blank node.
  - must have an `sh:path` pointing to an IRI of a property.
  - can have optionally these properties
    - `sh:datatype` 
    - `sh:maxCount` 
    - `sh:minCount`
- labels are taken from `dc:title` of the given shape, or from `rdfs:label` of the target class/path property.

## Minimal Example
```bash
export SHACL_FILE=src/test/resources/example.ttl
export OUTPUT_FILE=src/test/resources/example.puml
./gradlew run --args="$SHACL_FILE $OUTPUT_FILE"
```
This generates a PlantUML file taking into account all property shapes, and considering only data types as fields.

## Make a class visualized as a field
```bash
echo "SELECT ?field {} VALUES ?field { <http://www.w3.org/2001/XMLSchema#string> <http://www.w3.org/2004/02/skos/core#Concept> }" > fieldQuery.rq 
export SHACL_FILE=src/test/resources/example.ttl
export OUTPUT_FILE=src/test/resources/example.puml
./gradlew run --args="$SHACL_FILE $OUTPUT_FILE --fieldQuery=fieldQuery.rq"
```
Generates the same diagram, but showing `skos:Concept` as fields instead of nodes.

## Only visualize required edges/fields
```bash
export SHACL_FILE=src/test/resources/example.ttl
export OUTPUT_FILE=src/test/resources/example.puml
./gradlew run --args="$SHACL_FILE $OUTPUT_FILE --filterQuery=classpath:/edges-required-only.rq"
```
Filters out all constraints (edges/fields) that have `minCount < 1`.

## Generate TGF for advanced layout
```bash
export SHACL_FILE=src/test/resources/example.ttl
export OUTPUT_FILE=src/test/resources/example.tgf
./gradlew run --args="$SHACL_FILE --outputFormat tgf $OUTPUT_FILE"
```
Generates a TGF graph.
1. Open the TGF file in yEd editor.
2. Execute "Fit Node to Size" and "Hierarchical Layout" (or choose another layout)
3. Perform any manual editing/layout
4. Save the diagram as GraphML/Export it to the desired format (e.g. svg/png)