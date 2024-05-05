plugins {
    id("com.android.application")
}

android {
    namespace = "com.loopme.banner_sample"

    defaultConfig {
        minSdk = 24
        applicationId = "com.loopme.banner_sample.app"
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
    implementation("androidx.cardview:cardview:1.0.0")
}
