plugins {
    id("com.android.library")
}

android {
    namespace = "com.admob.mediation.adapters"
    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        buildConfigField("String", "VERSION_NAME", project.findProperty("VERSION_NAME") as? String ?: "")
    }
}

dependencies {
    api("com.google.android.gms:play-services-ads:23.1.0")
//    api(project(":loopme-sdk"))
    api("com.github.loopme.android-united-sdk:loopme-sdk:9.0.3")
}