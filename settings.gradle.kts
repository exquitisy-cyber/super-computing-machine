pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.minecraftforge.net/releases")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.neoforged.gradle.neoforge") {
                useModule("net.neoforged.gradle:neoforge-gradle-plugin:7.0.0")
            }
        }
    }
}

rootProject.name = "trnightmare-ego-autoclick"