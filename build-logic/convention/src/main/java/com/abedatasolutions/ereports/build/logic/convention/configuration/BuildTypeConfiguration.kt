package com.abedatasolutions.ereports.build.logic.convention.configuration

import com.abedatasolutions.ereports.build.logic.convention.BuildConfigField
import com.abedatasolutions.ereports.build.logic.convention.CommonExt
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.LibraryBuildType
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestBuildType
import com.android.build.api.dsl.TestExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

internal sealed class BuildTypeConfiguration {
    abstract val name: String
    protected open val inherit: BuildTypeConfiguration?
        get() = null
    protected open val fallbacks: List<BuildTypeConfiguration> = emptyList()
    protected open val minify: Boolean
        get() = inherit?.minify == true
    protected open val shrinkResources: Boolean
        get() = inherit?.shrinkResources == true

    private val fields: List<BuildConfigField> = emptyList()

    fun NamedDomainObjectContainer<out BuildType>.configure(ext: CommonExt){
        maybeCreate(name).apply(ext)
    }

    private sealed interface Application{
        fun ApplicationBuildType.applicationDefaults(ext: ApplicationExtension)
    }

    private data class StandardApplication(
        val signingConfigName: String? = null,
        val appIdSuffix: String? = null
    ):Application {
        override fun ApplicationBuildType.applicationDefaults(ext: ApplicationExtension) {
            applicationIdSuffix = appIdSuffix
            signingConfigName?.let {
                signingConfig = ext.signingConfigs.getByName(it)
            }
        }
    }

    private sealed interface LibBuildType{
        fun LibraryBuildType.libraryDefaults(ext: LibraryExtension)
    }

    private sealed interface TestingBuildType{
        fun TestBuildType.testDefaults(ext: TestExtension)
    }

    private fun BuildType.apply(ext: CommonExt){
        defaults(ext)
        if (this is ApplicationBuildType && ext is ApplicationExtension){
            isMinifyEnabled = minify
            isShrinkResources = shrinkResources
            if (this@BuildTypeConfiguration is Application) {
                applicationDefaults(ext)
            }
        }
        if (this is LibraryBuildType && ext is LibraryExtension){
            isMinifyEnabled = false
            isShrinkResources = false
            if (this@BuildTypeConfiguration is LibBuildType) {
                libraryDefaults(ext)
            }
        }
        if (this is TestBuildType && ext is TestExtension) {
            isMinifyEnabled = false
            isShrinkResources = false
            if (this@BuildTypeConfiguration is TestingBuildType) {
                testDefaults(ext)
            }
        }
        if (minify) proguardFiles(
            ext.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }

    private fun BuildType.defaults(ext: CommonExt){
        inherit?.let {
            initWith(ext.buildTypes.getByName(it.name))
        }
        if(ext.buildFeatures.buildConfig == true) fields.forEach {
            buildConfigField(it.type.generic, it.name, it.wrappedValue)
        }
        matchingFallbacks += fallbacks.map { it.name }
    }

    data object Release: BuildTypeConfiguration(), Application by StandardApplication(
        signingConfigName = "debug"
    ){
        override val name: String
            get() = "release"
        override val minify: Boolean
            get() = false
        override val shrinkResources: Boolean
            get() = false
    }

    data object Debug: BuildTypeConfiguration(){
        override val name: String
            get() = "debug"
    }

    data object Staging: BuildTypeConfiguration(), Application by StandardApplication(
        signingConfigName = "debug",
        appIdSuffix = ".staging"
    ){
        override val name: String
            get() = "staging"
        override val inherit: BuildTypeConfiguration
            get() = Release
        override val fallbacks: List<BuildTypeConfiguration>
            get() = listOf(
                Release
            )
    }

    companion object {
        val common: List<BuildTypeConfiguration>
            get() = listOf(
                Debug,
                Release
            )
        val custom: List<BuildTypeConfiguration>
            get() = listOf(
                Staging
            )

        fun Project.configureBuildTypes(
            commonExtension: CommonExt,
            configs: List<BuildTypeConfiguration> = common + custom
        ){
            commonExtension.run {
                buildTypes {
                    configs.forEach {
                        it.run {
                            configure(commonExtension)
                        }
                    }
                }
            }
        }
    }
}