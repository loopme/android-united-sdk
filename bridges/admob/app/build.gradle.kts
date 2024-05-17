plugins {
    id("com.android.application")
}

android {
    namespace = "com.loopme.admob.app"

    defaultConfig {
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
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("com.google.android.gms:play-services-ads:23.0.0")
//    implementation(project(":loopme-sdk"))
}