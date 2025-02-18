plugins {
    alias(libs.plugins.ereports.android.library)
}

android {
    namespace = "com.abedatasolutions.ereports.shared"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}