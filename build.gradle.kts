// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("1.9.24")
    repositories {
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

plugins {
    id("com.android.application") version "7.4.2" apply false
}

//allprojects {
//    repositories {
//        mavenCentral()
//        google()
//    }
//}
