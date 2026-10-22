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
    version = "21.1.250"
    mods {
        create("trnightmareegoautoclick") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "ModLoader" to "neoforge",
            "NeoForge-Version" to "21.1.250",
            "Mod-ID" to "trnightmareegoautoclick",
            "Mod-Name" to "TrNightmare Ego Auto Clicker",
            "Mod-Version" to "1.0.0"
        )
    }
}