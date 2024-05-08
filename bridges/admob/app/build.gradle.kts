plugins {
    id("com.android.application")
}

android {
    namespace = "com.loopme.admob.app"

    defaultConfig {
        minSdk = 24
        applicationId = "com.loopme.admob.app"
        versionCode = 1
        versionName ="1.0"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        aidl = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
        }
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(project(":bridges:admob:adapter"))
//    implementation("com.google.android.gms:play-services-ads:23.0.0")
//    implementation(project(":loopme-sdk"))
}