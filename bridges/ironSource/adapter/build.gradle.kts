plugins {
    id("com.android.library")
}

android {
    namespace = "com.ironsource.adapters.custom.loopme"
    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        buildConfigField("String", "VERSION_NAME", project.findProperty("VERSION_NAME") as? String ?: "")
    }
}

dependencies {
    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("androidx.appcompat:appcompat:1.6.0")
    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("com.google.android.material:material:1.6.0")

    api("com.ironsource.sdk:mediationsdk:8.0.0")
    api(project(":loopme-sdk"))
//    api("com.github.loopme.android-united-sdk:loopme-sdk:9.0.8")
}