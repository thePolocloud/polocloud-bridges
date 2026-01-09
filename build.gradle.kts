import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("com.gradleup.shadow") version "9.0.0"
    kotlin("jvm") version "2.3.0"
    `maven-publish`
}

allprojects {
    group = "dev.httpmarco.polocloud"
    version = "3.0.0-pre.8-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            name = "polocloud-snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }

}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    if (name != "bridge-api" && name != "bridge-fabric") {
        apply(plugin = "com.gradleup.shadow")

        tasks.shadowJar {
            archiveClassifier.set(null)
        }
    }
    apply(plugin = "maven-publish")

    dependencies {
        api("dev.httpmarco.polocloud:sdk-java:$version")
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks.withType<ShadowJar>().configureEach {
        isZip64 = true
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                when {
                    name == "bridge-fabric" -> {
                        artifact(tasks.named("mergeFabricVersions")) {
                            classifier = null
                        }
                    }

                    plugins.hasPlugin("com.gradleup.shadow") -> {
                        artifact(tasks.named("shadowJar")) {
                            classifier = null
                        }
                    }

                    else -> {
                        from(components["java"])
                    }
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
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/releases/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))

            username.set(System.getenv("ossrhUsername") ?: "")
            password.set(System.getenv("ossrhPassword") ?: "")
        }
    }
    useStaging.set(!version.toString().endsWith("-SNAPSHOT"))
}

