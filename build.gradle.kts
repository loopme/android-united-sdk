import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension

plugins {
    id("java")
    id("maven-publish")
    id("com.android.library") version "8.4.1" apply false
}

group = "com.github.loopme.android-united-sdk"

fun Project.android(configuration: BaseExtension.() -> Unit) =
    extensions.getByName<BaseExtension>("android").configuration()

fun Project.androidLibrary(configuration: LibraryExtension.() -> Unit) =
    extensions.getByName<LibraryExtension>("android").configuration()

subprojects {
    if (name in setOf("bridges", "ironSource", "appLovin", "admob")) {
        return@subprojects
    }
    afterEvaluate {
        android {
            compileSdkVersion(34)
            buildToolsVersion = "34.0.0"
            defaultConfig {
                if (minSdkVersion == null)
                    minSdk = 21
                targetSdk = 34
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }

        if (plugins.hasPlugin("com.android.library")) {
            apply(plugin = "maven-publish")
            androidLibrary {
                buildFeatures {
                    buildConfig = true
                }
                publishing {
                    singleVariant("release") {
                        withSourcesJar()
                    }
                }
            }

            publishing {
                publications {
                    register<MavenPublication>("loopme") {
                        afterEvaluate {
                            from(components["release"])
                        }
                        groupId = "com.github.loopme.android-united-sdk"
                        artifactId = if (project.name == "adapter") {
                            project.parent?.name + "-" + project.name
                        } else {
                            project.name
                        }
                    }
                }
            }
        }
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}