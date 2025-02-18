package com.abedatasolutions.ereports.build.logic.convention.configuration

import com.abedatasolutions.ereports.build.logic.convention.CommonExt
import com.abedatasolutions.ereports.build.logic.convention.ProjectDefaults
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExt
) {
    commonExtension.apply {
        compileSdk = ProjectDefaults.COMPILE_SDK

        defaultConfig {
            minSdk = ProjectDefaults.MIN_SDK
        }

        compileOptions {
//            isCoreLibraryDesugaringEnabled = true
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    configureKotlin()

    dependencies {
//        "coreLibraryDesugaring"(libs.findLibrary("desugar.jdk.libs").get())
    }
}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}