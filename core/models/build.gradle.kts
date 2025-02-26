plugins {
    alias(libs.plugins.ereports.jvm.library)
    alias(libs.plugins.ksp)
    id(libs.plugins.kotlin.plugin.serialization.get().pluginId)
}
dependencies {
    implementation(libs.kotlinx.serialization.json)
    api(libs.kotlinx.datetime)
    implementation(libs.ktor.client.core)
    api(projects.core.common)
}
