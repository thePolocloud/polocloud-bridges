import java.util.Properties

val props = Properties()
file("$projectDir/gradle.properties").inputStream().use { props.load(it) }

val versions = props.getProperty("dev.httpmarco.polocloud.bridges.fabric.minecraft-versions").split(";")

val mixinsJson = versions.joinToString(prefix = "[", postfix = "]", separator = ",") {
    "\"polocloud_bridge_${it.replace(".", "_")}.mixins.json\""
}

val mainClassesJson = versions.joinToString(prefix = "[", postfix = "]", separator = ",") { version ->
    """{"adapter":"kotlin","value":"dev.httpmarco.polocloud.bridges.fabric.v${version.replace(".", "_")}.FabricBridge"}"""
}

val placeholders = mapOf(
    "version" to project.version.toString(),
    "mixins" to mixinsJson,
    "mainClasses" to mainClassesJson
)

dependencies {
    compileOnly(projects.bridges.bridgeApi)
    compileOnly(libs.gson)
}

tasks.processResources {
    filteringCharset = "UTF-8"
    inputs.properties(placeholders)
    filesMatching("fabric.mod.json") {
        expand(placeholders)
    }
}

tasks.register<Jar>("mergeFabricVersions") {
    archiveFileName.set("polocloud-${project.name}-$version.jar")

    val remappedJars = subprojects.flatMap { sub ->
        val remapTask = sub.tasks.named("remapJar").get()
        dependsOn(remapTask)
        remapTask.outputs.files
    }

    // we dont need the jsons from the different versions cause this causes conflicts
    from(remappedJars.map { zipTree(it) }) {
        exclude("fabric.mod.json")
        exclude("META-INF/MANIFEST.MF")
    }

    // put the normal code such as config in the jar
    from("${layout.buildDirectory.get()}/classes/java/main")
    from("${layout.buildDirectory.get()}/classes/kotlin/main")

    // we replace the placeholders here
    dependsOn("processResources")

    // we only take the json from our main source
    from("${layout.buildDirectory.get()}/resources/main") {
        include("fabric.mod.json")
        include("bridge.json")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // remove signatures
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.EC", "META-INF/*.RSA")
}

tasks.named<Jar>("jar") {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            artifact(tasks.named("mergeFabricVersions")) {
                classifier = null
            }

            pom {
                description.set("PoloCloud gRPC API with bundled dependencies")
                url.set("https://github.com/thePolocloud/polocloud")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        name.set("Mirco Lindenau")
                        email.set("mirco.lindenau@gmx.de")
                    }
                }
                scm {
                    url.set("https://github.com/thePolocloud/polocloud")
                    connection.set("scm:git:https://github.com/thePolocloud/polocloud.git")
                    developerConnection.set("scm:git:https://github.com/thePolocloud/polocloud.git")
                }
            }
        }
    }
}