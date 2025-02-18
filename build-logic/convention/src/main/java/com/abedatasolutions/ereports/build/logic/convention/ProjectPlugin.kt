package com.abedatasolutions.ereports.build.logic.convention

internal enum class ProjectPlugin(val id: String) {
    KotlinAndroid("org.jetbrains.kotlin.android"),
    AndroidApp("com.android.application"),
    AndroidLib("com.android.library"),
    AndroidTest("com.android.test"),
    AndroidBase("com.android.base"),
    JvmLib("org.jetbrains.kotlin.jvm"),
    Ksp("com.google.devtools.ksp"),
    Compose("org.jetbrains.kotlin.plugin.compose"),
    Serialization("org.jetbrains.kotlin.plugin.serialization"),
}