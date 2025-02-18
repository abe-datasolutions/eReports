package com.abedatasolutions.ereports.build.logic.convention.configuration.flavor

import com.abedatasolutions.ereports.build.logic.convention.BuildConfigField
import com.abedatasolutions.ereports.build.logic.convention.CommonExt
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.LibraryProductFlavor
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.dsl.TestExtension
import com.android.build.api.dsl.TestProductFlavor
import org.gradle.api.NamedDomainObjectContainer

internal sealed interface FlavorConfiguration {
    val dimensionName: String
    val flavorName: String
    val fields: List<BuildConfigField>
        get() = emptyList()

    private fun ProductFlavor.defaults(ext: CommonExt) {
        dimension = dimensionName
        if(ext.buildFeatures.buildConfig == true) fields.forEach {
            buildConfigField(it.type.name, it.name, it.wrappedValue)
        }
    }

    interface Application{
        fun ApplicationProductFlavor.applicationDefaults(ext: CommonExt)
    }

    data class StandardApplication(
        val appIdSuffix: String? = null,
        val vNameSuffix: String? = null,
        val vCode: Int? = null,
    ): Application {
        override fun ApplicationProductFlavor.applicationDefaults(ext: CommonExt) {
            applicationIdSuffix = appIdSuffix
            versionNameSuffix = vNameSuffix
            vCode?.let {
                versionCode = it
            }
        }
    }

    interface Library{
        fun LibraryProductFlavor.libraryDefaults(ext: CommonExt)
    }

    interface Test{
        fun TestProductFlavor.testDefaults(ext: CommonExt)
    }

    companion object {
        fun NamedDomainObjectContainer<out ProductFlavor>.applyConfiguration(
            ext: CommonExt,
            configuration: FlavorConfiguration
        ){
            configuration.run {
                maybeCreate(flavorName).apply{
                    defaults(ext)
                    if (this is ApplicationProductFlavor && ext is ApplicationExtension){

                        if (this@run is Application) applicationDefaults(ext)
                    }
                    if (this is LibraryProductFlavor && ext is LibraryExtension){

                        if (this@run is Library) libraryDefaults(ext)
                    }
                    if (this is TestProductFlavor && ext is TestExtension){

                        if (this@run is Test) testDefaults(ext)
                    }
                }
            }
        }
    }
}
