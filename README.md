# SHACL Viz

A simple UML-like diagram generator for SHACL data model backed by OWL ontologies, resulting to the following form:
- nodes correspond to ontology classes (`sh:targetClass` of `sh:NodeShape`s)
  - their labels are taken from `dc:title` of the node shapes (falling back to `rdfs:label` of the `sh:targetClass`)
  - class labels contain also fields which are generated from `sh:PropertyShape`s for data type ranges. 
- edges correspond to `sh:PropertyShape`s 
  - their labels are taken from `sh:name` (falling back to `sh:description`)

The generation is configurable, see [sample configuration](src/main/kotlin/zone/cogni/shacl/diagram/acqf/configurations/OverviewConfiguration.kt):
- `filter` - filters domainClass-property-rangeClass combinations that will be ignored
- `fieldClasses` - property IRIs that will be visualized as fields (not edges) to make the diagram more understandable. 

The output graph format is TGF graph (importable by yEd) and are fully customizable (including various layouting options) in yEd.

## Usage
1. Generate diagram as shown in section Diagram Generation.
2. Open the file in yEd editor.
3. Execute "Fit Node to Size" and "Hierarchical Layout"
4. Save the diagram as GraphML/Export it to the desired format (e.g. svg/png)

## Diagram Generation

### General
For arbitrary SHACL file (importing and OWL ontology) you can generate yEd diagram as follows:

`./gradlew run --args="../acqf.ttl output.tgf"`

### ACQF
For ACQF, there are predefined diagram configurations:

Generate only overview model

`./gradlew run --args="../acqf.ttl ../generated/graph-overview.tgf zone.cogni.shacl.diagram.acqf.configurations.OverviewConfiguration"`

- Generate only required fields (plus selected optional ones) ACQF model

`./gradlew run --args="../acqf.ttl ../generated/graph-required.tgf zone.cogni.shacl.diagram.acqf.configurations.RequiredConfiguration"`

Generate full ACQF model

`./gradlew run --args='../acqf.ttl ../generated/graph.tgf zone.cogni.shacl.diagram.acqf.configurations.FullConfiguration'`