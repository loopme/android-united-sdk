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

    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    api("androidx.appcompat:appcompat:1.6.0")
    api("androidx.core:core:1.6.0")

    api("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.preference:preference:1.2.1")

    implementation("com.google.android.gms:play-services-ads-identifier:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.google.androidbrowserhelper:androidbrowserhelper:2.5.0")

    testImplementation("androidx.test.ext:junit:1.2.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("junit:junit:4.13.2")

    testImplementation("org.robolectric:robolectric:4.13")
    androidTestImplementation("org.robolectric:robolectric:4.13")
}