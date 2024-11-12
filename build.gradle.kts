plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    id("org.owasp.dependencycheck") version "10.0.3"
    id("maven-publish")
    id("signing")
    id("pl.allegro.tech.build.axion-release") version "1.13.7"
    id("org.jetbrains.dokka") version "1.8.10"
}

group = "zone.cogni.semanticz"

// Configure the axion-release plugin
scmVersion {
    tag.apply {
        prefix = "v"
        versionSeparator = ""
        branchPrefix.set("release/.*", "release-v")
        branchPrefix.set("hotfix/.*", "hotfix-v")
    }
    nextVersion.apply {
        suffix = "SNAPSHOT"
        separator = "-"
    }
    versionIncrementer("incrementPatch") // Increment the patch version
}

// Set the project version from scmVersion
version = scmVersion.version

repositories {
    mavenCentral()
}

// Create a sources JAR
val kotlinSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.main.get().kotlin)
}

// Create a Javadoc JAR using Dokka
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    dependsOn(tasks.named("dokkaJavadoc"))
    from(tasks.named("dokkaJavadoc").get().outputs.files)
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.clikt.markdown)
    implementation(libs.slf4j.api)
    runtimeOnly(libs.slf4j.simple)
    implementation(libs.jena.arq)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

// Publishing configuration including Cognizone Nexus and Maven Central
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifact(kotlinSourcesJar)
            artifact(javadocJar)

            pom {
                name.set("semanticz-shaclviz")
                description.set("A tool to create flexible SHACL diagrams in PlantUML or yEd")
                url.set("https://github.com/cognizone/semanticz-shaclviz")

                scm {
                    connection.set("scm:git@github.com/cognizone/semanticz-shaclviz.git")
                    developerConnection.set("scm:git@github.com/cognizone/semanticz-shaclviz.git")
                    url.set("https://github.com/cognizone/semanticz-shaclviz.git")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("cognizone")
                        name.set("Cognizone")
                        email.set("semanticz@cogni.zone")
                    }
                }
            }
        }
    }

    repositories {
        // Cognizone Nexus repository
        if (project.hasProperty("publishToCognizoneNexus")) {
            maven {
                credentials {
                    username = System.getProperty("nexus.username")
                    password = System.getProperty("nexus.password")
                }
                val releasesRepoUrl = "${System.getProperty("nexus.url")}/repository/cognizone-release"
                val snapshotsRepoUrl = "${System.getProperty("nexus.url")}/repository/cognizone-snapshot"
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                isAllowInsecureProtocol = true
            }
        }

        // Maven Central repository
        if (project.hasProperty("publishToMavenCentral")) {
            maven {
                credentials {
                    username = System.getProperty("ossrh.username")
                    password = System.getProperty("ossrh.password")
                }
                val stagingRepoUrl = "${System.getProperty("ossrh.url")}/service/local/staging/deploy/maven2"
                val snapshotsRepoUrl = "${System.getProperty("ossrh.url")}/content/repositories/snapshots"
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else stagingRepoUrl)
            }
        }
    }
}

// Signing configuration
extensions.configure<SigningExtension> {
    if (project.hasProperty("publishToMavenCentral") || project.hasProperty("publishToCognizoneNexus")) {
        sign(extensions.getByType<PublishingExtension>().publications["mavenJava"])
    }
}

// Include LICENSE file in META-INF folder of the jar
tasks.jar {
    from(projectDir) {
        include("LICENSE")
        into("META-INF")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("zone.cogni.semanticz.shaclviz.CLIKt")
}
