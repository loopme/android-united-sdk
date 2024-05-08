plugins {
    id("com.android.library")
}

android {
    namespace = "com.loopme"
    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        buildConfigField("String", "OM_SDK_JS_URL", project.findProperty("OM_SDK_JS_URL") as? String ?: "")
        buildConfigField("String", "OM_SDK_PARTNER", project.findProperty("OM_SDK_PARTNER") as? String ?: "")
        buildConfigField("String", "VERSION_NAME", project.findProperty("VERSION_NAME") as? String ?: "")
    }
}

dependencies {
    implementation(files("libs/omsdk-android-1.3.3-loopme.jar"))

    api("androidx.appcompat:appcompat:1.6.1")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("androidx.annotation:annotation:1.7.1")

    implementation("androidx.preference:preference:1.2.1")

    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    implementation("com.google.androidbrowserhelper:androidbrowserhelper:2.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}