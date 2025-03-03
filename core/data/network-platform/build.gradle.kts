plugins {
    alias(libs.plugins.ereports.android.library)
    alias(libs.plugins.ereports.android.flavor)
}

android {
    namespace = "com.abedatasolutions.ereports.core.data.network.platform"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    api(projects.core.data.network)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}