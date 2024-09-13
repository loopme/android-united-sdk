plugins {
    id("com.android.library")
}

android {
    namespace = "com.applovin.mediation.adapters.loopme"
    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        buildConfigField("String", "VERSION_NAME", project.findProperty("VERSION_NAME") as? String ?: "")
    }
}

dependencies {
//    api(project(":loopme-sdk"))
    api("com.github.loopme.android-united-sdk:loopme-sdk:9.0.6")
    //noinspection GradleDynamicVersion
    api("com.applovin:applovin-sdk:12.+")

    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("androidx.appcompat:appcompat:1.6.0")
    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("com.google.android.material:material:1.6.0")
}