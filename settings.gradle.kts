
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        embeddedKotlin("android")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://android-sdk.is.com/")
        }
        maven {
            url = uri("https://jitpack.io/")
        }
//        mavenLocal()
    }
}


include(
    // LoopMe SDK
    ":loopme-sdk",
    // LoopMe SDK Sample Apps
    ":sample-interstitial",
    ":sample-sdk",
    // IronSource Mediation Adapter and Sample App
    ":bridges:ironSource:adapter",
    ":bridges:ironSource:app",
    // AppLovin Mediation Adapter and Sample App
    ":bridges:appLovin:adapter",
    ":bridges:appLovin:app",
    // AdMob Mediation Adapter and Sample App
    ":bridges:admob:adapter",
    ":bridges:admob:app"
)