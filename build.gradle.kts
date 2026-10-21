plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.148"
}

group = "mods.trnightmareegoautoclick"
version = "1.0.0"

base {
    archivesName = "trnightmareegoautoclick"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
}

neoForge {
    // Pinned to the exact NeoForge build this mod is meant to run against
    version = "21.1.262"

    mods {
        create("trnightmareegoautoclick") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
