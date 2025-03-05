import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm") version "1.9.10"

    // For serialization: remove if not needed
    kotlin("plugin.serialization") version "1.9.10"

    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "luna724.iloveichika.binsniper"
archivesName = "Luna's BinSniper"
version = "2.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
            enableLanguageFeature("BreakContinueInInlineLambdas")
        }
    }
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

loom {
    launchConfigs {
        "client" {
            property("asmhelper.verbose", "true")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    }
}

sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/java/main"))
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.sk1er.club/repository/maven-public")

    // If you don't want to log in with your real minecraft account, remove this line
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")

    maven("https://repo.nea.moe/releases")
    maven("https://maven.notenoughupdates.org/releases")
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val shadowModImpl: Configuration by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

val devenvMod: Configuration by configurations.creating {
    isTransitive = false
    isVisible = false
}

dependencies {
    // Dependencies
    shadowImpl("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    shadowImpl("com.google.code.gson:gson:2.10.1")
    shadowImpl("com.moandjiezana.toml:toml4j:0.7.2")
    shadowImpl("org.apache.commons:commons-lang3:3.10")
    shadowImpl("com.fasterxml.jackson.core:jackson-core:2.8.11")
    shadowImpl("com.fasterxml.jackson.module:jackson-module-kotlin:2.8.11")
    shadowImpl("com.fasterxml.jackson.core:jackson-databind:2.8.11")
    shadowImpl("net.md-5:bungeecord-config:1.14-SNAPSHOT")
    shadowImpl("org.json:json:20230227")

    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    // essential
    shadowImpl("gg.essential:loader-launchwrapper:1.1.3")
    implementation("gg.essential:essential-1.8.9-forge:3662")

    // other
    implementation(kotlin("stdlib-jdk8"))
    shadowImpl("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") {
        exclude(group = "org.jetbrains.kotlin")
    }

    shadowModImpl("org.notenoughupdates.moulconfig:legacy:3.0.0-beta.7")
    implementation(files("libs/Skytils.jar"))
}

tasks.compileJava {
    dependsOn(tasks.processResources)
}


tasks.processResources {
        filesMatching("mcmod.info") {
            expand(
                mapOf(
                    "modname" to project.name,
                    "modid" to project.name.lowercase(),
                    "version" to project.version,
                    "mcversion" to "1.8.9"
                )
            )
        }
        rename("(.+_at.cfg)", "META-INF/$1")
        exclude("**/*.pyx")
        exclude(".idea/**")
        exclude("**/__pycache__/**")
        exclude("**/.idea/**")
        exclude("**/*.gitignore")
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    archiveBaseName.set("Luna's BinSniper")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        this["TweakClass"] = "gg.essential.loader.stage0.EssentialSetupTweaker"
        this["TweakOrder"] = "0"
    }
}

val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl, shadowModImpl)
    doLast {
        configurations.forEach {
            println("Config: ${it.files}")
        }
        exclude("META-INF/versions/**")


    }
    // If you want to include other dependencies and shadow them, you can relocate them in here
    relocate("io.github.moulberry.moulconfig", "luna724.iloveichika.shade.moulconfig")
}

tasks.jar {
    archiveClassifier.set("nodeps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.assemble.get().dependsOn(tasks.remapJar)