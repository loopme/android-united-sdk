plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.loopme"
    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        buildConfigField("String", "OM_SDK_JS_URL", project.findProperty("OM_SDK_JS_URL") as? String ?: "")
        buildConfigField("String", "OM_SDK_PARTNER", project.findProperty("OM_SDK_PARTNER") as? String ?: "")
        buildConfigField("String", "VERSION_NAME", project.findProperty("VERSION_NAME") as? String ?: "")
    }

    viewBinding {
        enable = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(files("libs/omsdk-android-1.3.3-loopme.jar"))

    // AndroidX Libraries
    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    api("androidx.appcompat:appcompat:1.6.0")
    api("androidx.core:core:1.6.0")
    api("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.preference:preference:1.2.1")

    // Media and ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-datasource-okhttp:1.4.1")

    // Google Services
    implementation("com.google.android.gms:play-services-ads-identifier:18.1.0")

    // Browser Helper
    implementation("com.google.androidbrowserhelper:androidbrowserhelper:2.5.0")

    // Testing Libraries
    testImplementation("androidx.test.ext:junit:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20220924")
    testImplementation("org.robolectric:robolectric:4.13")
    androidTestImplementation("org.robolectric:robolectric:4.13")
}