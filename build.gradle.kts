plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("com.gradleup.shadow") version "9.3.0"
    kotlin("jvm") version "2.2.21"
    `maven-publish`
}

allprojects {
    group = "dev.httpmarco.polocloud"
    version = "3.0.0-pre.8-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            name = "polocloud-snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }

}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    if (name != "bridge-api" || name != "bridge-fabric") {
        apply(plugin = "com.gradleup.shadow")

        tasks.shadowJar {
            archiveClassifier.set(null)

            mergeServiceFiles()
        }
    }
    apply(plugin = "maven-publish")

    dependencies {
        api("dev.httpmarco.polocloud:sdk-java:3.0.0-pre.8-SNAPSHOT")
    }

    kotlin {
        jvmToolchain(21)
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

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

