package com.abedatasolutions.ereports.build.logic.convention.configuration.flavor

import com.abedatasolutions.ereports.build.logic.convention.BuildConfigField
import com.abedatasolutions.ereports.build.logic.convention.BuildConfigType
import com.abedatasolutions.ereports.build.logic.convention.CommonExt
import com.abedatasolutions.ereports.build.logic.convention.configuration.flavor.FlavorConfiguration.Companion.applyConfiguration
import org.gradle.api.Project

internal sealed class Server: FlavorConfiguration {
    final override val dimensionName: String = "Server"
    protected abstract val baseUrl: String
    final override val fields: List<BuildConfigField>
        get() = listOf(
            BuildConfigField(BuildConfigType.String, "BASE_URL", baseUrl)
        )

    data object LaLive: Server(){
        override val flavorName: String = "la_live"
        override val baseUrl: String = "https://www.abclab.com"
    }

    data object JktDev: Server(){
        override val flavorName: String = "jkt_dev"
        override val baseUrl: String = "https://jkt-dev-inetrep.abclab.com.ph"
    }

    data object JktBeta: Server(){
        override val flavorName: String = "jkt_beta"
        override val baseUrl: String = "https://jkt-beta-inetrep.abclab.com.ph"
    }

    data object JktLive: Server(){
        override val flavorName: String = "jkt_live"
        override val baseUrl: String = "https://jkt-dev-inetrep.abclab.com.ph" //FIXME
    }

    companion object{
        val flavors: List<Server>
            get() = listOf(
                LaLive,
                JktDev,
                JktBeta
            )

        fun Project.configureServerFlavors(commonExt: CommonExt){
            val dimensions = flavors.map { it.dimensionName }.distinct()
            commonExt.run {
                flavorDimensions += dimensions
                productFlavors {
                    flavors.forEach {
                        applyConfiguration(commonExt, it)
                    }
                }
            }
        }
    }
}