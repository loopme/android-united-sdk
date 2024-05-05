plugins {
    id("com.android.application")
}

android {
    namespace = "com.loopme.interstitial_sample"

    defaultConfig {
        minSdk = 24
        applicationId = "com.loopme.interstitial_sample"
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
    implementation(project(":loopme-sdk"))
//    implementation("com.github.loopme.android-united-sdk:loopme-sdk:unspecified")
}