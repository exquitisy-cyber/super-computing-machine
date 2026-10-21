pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.minecraftforge.net/releases")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.neoforged.gradle.neoforge") {
                useModule("net.neoforged.gradle:neoforge-gradle-plugin:21.1.45")
            }
        }
    }
}

rootProject.name = "trnightmare-ego-autoclick"