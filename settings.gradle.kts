pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // hoặc DISABLE_REPOS_CHECK nếu muốn
    repositories {
        google()
        mavenCentral()
    }
}


rootProject.name = "Yahooooo"
include(":app")
 