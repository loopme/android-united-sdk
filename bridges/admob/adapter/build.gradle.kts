plugins {
    id("com.android.library")
}

android {
    namespace = "com.admob.mediation.adapters"
}

dependencies {
    api("com.google.android.gms:play-services-ads:23.1.0")
    api(project(":loopme-sdk"))
}