plugins {
    alias(libs.plugins.ereports.jvm.library)
}
dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
}