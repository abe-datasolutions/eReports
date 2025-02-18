import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}
group = "com.abedatasolutions.ereport.build.logic"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradle.plugin)
//    compileOnly(libs.compose.compiler.gradle.plugin) //FIXME: Upgrade Kotlin Version to 2.1.20
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication"){
            id = "ereports.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary"){
            id = "ereports.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidTest"){
            id = "ereports.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("jvmLibrary"){
            id = "ereports.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    languageVersion = KotlinVersion.KOTLIN_1_9
}