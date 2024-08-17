plugins {
    id("com.android.application")
}

android {
    namespace = "com.loopme.applovin.app"

    defaultConfig {
        applicationId = "com.loopme.applovin.app"
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
    implementation(project(":bridges:appLovin:adapter"))
//    implementation("com.github.loopme.android-united-sdk:appLovin-adapter:AL_0.0.1")

    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("androidx.appcompat:appcompat:1.6.0")
    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("com.google.android.material:material:1.6.0")
}