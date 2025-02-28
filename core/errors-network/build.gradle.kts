plugins {
    alias(libs.plugins.ereports.jvm.library)
}
dependencies {
    api(projects.core.errors)
    api(projects.core.models)
}
