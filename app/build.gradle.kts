plugins {
    alias(libs.plugins.ereports.android.application)
    alias(libs.plugins.ereports.android.build.type)
    id(libs.plugins.kotlin.plugin.serialization.get().pluginId)
}
android {
    namespace = "com.abclab.abcereports"
    defaultConfig {
        applicationId = "com.abclab.abcereports"
        buildConfigField(
            type = "int",
            name = "BRANCH_ID",
            value = "2" // PH = 1, ID = 2, US = 0
        )

        buildConfigField(
            type = "String",
            name = "PREF_COUNTRY",
            value = "\"ID\"" // PH, ID, US
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.sqliteassethelper)
    implementation(projects.shared)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
