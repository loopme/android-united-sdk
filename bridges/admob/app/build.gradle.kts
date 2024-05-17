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
//    implementation(project(":bridges:admob:adapter"))
    implementation("com.github.loopme.android-united-sdk:admob-adapter:AM_0.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}