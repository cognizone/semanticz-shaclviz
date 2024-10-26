plugins {
    application
    kotlin("jvm") version "1.9.23"
}

group = "zone.cogni.semanticz"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:2.0.12")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.12")
    implementation("org.apache.jena:jena-arq:5.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("zone.cogni.semanticz.shaclviz.GenerateDiagramKt")
}