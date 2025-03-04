plugins {
    alias(libs.plugins.ereports.android.library)
}

android {
    namespace = "com.abedatasolutions.ereports.core.common.platform"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    api(projects.core.common)
    testImplementation(projects.core.commonTest)
    androidTestImplementation(projects.core.commonTest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}