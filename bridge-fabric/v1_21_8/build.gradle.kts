import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("fabric-loom") version "1.14-SNAPSHOT"
}

val minecraftVersion = "1.21.8"
val fabricMappingsVersion = "1.21.8+build.1"
val fabricApiVersion = "0.131.0+1.21.8"

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$fabricMappingsVersion")

    modImplementation(libs.bundles.fabric)
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modCompileOnly(fabricApi.module("fabric-networking-api-v1", fabricApiVersion))
    include(fabricApi.module("fabric-api-base", fabricApiVersion))
    include(fabricApi.module("fabric-networking-api-v1", fabricApiVersion))

    compileOnly(projects.bridges.bridgeFabric)
}

loom {
    serverOnlyMinecraftJar()
    mixin {
        useLegacyMixinAp.set(true) // this brings the refmap back
        defaultRefmapName.set("v1_21_8-refmap.json") // this brings the refmap back
    }
}

tasks.named<RemapJarTask>("remapJar") {
    archiveFileName.set("polocloud-${project.name}-$version.jar")
}