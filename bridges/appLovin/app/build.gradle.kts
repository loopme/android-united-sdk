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
//    implementation(project(":bridges:appLovin:adapter"))
    implementation("com.github.loopme.android-united-sdk:appLovin-adapter:AL_0.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}