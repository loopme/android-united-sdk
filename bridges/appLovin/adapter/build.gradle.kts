plugins {
    id("com.android.library")
}

android {
    namespace = "com.applovin.mediation.adapters"
    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    api(project(":loopme-sdk"))
    api("com.applovin:applovin-sdk:12.4.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}