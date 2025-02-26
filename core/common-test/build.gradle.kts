plugins {
    alias(libs.plugins.ereports.jvm.library)
}
dependencies {
    api(projects.core.common)
    api(libs.junit)
    api(libs.assertk)
    api(libs.kotlinx.coroutines.test)
}