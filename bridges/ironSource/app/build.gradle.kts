plugins {
    id("com.android.application")
}

android {
    namespace = "com.loopme.ironsource_mediation_sample"

    defaultConfig {
        applicationId = "com.loopme.ironsource_mediation_sample"
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
    implementation(project(":bridges:ironSource:adapter"))
//    implementation("com.github.loopme.android-united-sdk:ironSource-adapter:IS_0.0.2")

    implementation("com.ironsource.sdk:mediationsdk:8.0.0")

    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("androidx.appcompat:appcompat:1.6.0")
    //noinspection GradleDependency - appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("com.google.android.material:material:1.6.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}