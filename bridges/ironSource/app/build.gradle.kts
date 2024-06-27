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

    // appcompat and material > 1.6.0 does not supported by Unity 2021
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.6.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}