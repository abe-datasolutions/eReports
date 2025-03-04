import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

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

tasks.test {
    val properties = gradleLocalProperties(
        rootProject.projectDir,
        providers
    )
    environment(
        "VALID_USER",
        properties.getProperty("VALID_USER").toString()
    )
    environment(
        "VALID_PASSWORD",
        properties.getProperty("VALID_PASSWORD").toString()
    )
    environment(
        "TEST_BASE_URL",
        properties.getProperty("TEST_BASE_URL").toString()
    )
    useJUnit()
}