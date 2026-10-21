plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("net.neoforged.gradle.neoforge") version "21.1.45"
}

group = "mods.trnightmareegoautoclick"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.minecraftforge.net/releases")
}

dependencies {
    minecraft("net.minecraft:client:1.21.1-20240920.165612")
    mappings("net.neoforged:neoforge:1.21.1-21.1.45:mappings")
    modImplementation("net.neoforged:neoforge:1.21.1-21.1.45")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "21"
            freeCompilerArgs += listOf("-Xjsr305=strict")
        }
    }
    remapJar {
        mappings.set("official")
    }
}

neoforge {
    modId = "trnightmareegoautoclick"
    modName = "TrNightmare Ego Auto Clicker"
    modDescription = "Automatically clicks silver/gold nodes in the Ego Converse minigame"
    version = "1.0.0"
    authors = listOf("exquitisy-cyber")
    mcVersion = "1.21.1"
    neoforgeVersion = "21.1.45"
    javaVersion = 21
}