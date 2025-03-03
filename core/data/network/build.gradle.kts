plugins {
    alias(libs.plugins.ereports.jvm.library)
}

dependencies {
    implementation(libs.bundles.ktor)
    api(projects.core.models)
    api(projects.core.errorsNetwork)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    testImplementation(projects.core.commonTest)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}