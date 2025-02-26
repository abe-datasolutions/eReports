plugins {
    alias(libs.plugins.ereports.jvm.library)
}

dependencies {
    implementation(libs.bundles.ktor)
    api(projects.core.models)
}