package com.abedatasolutions.ereports.build.logic.convention.configuration

import com.android.build.api.variant.AndroidComponentsExtension

internal fun AndroidComponentsExtension<*, *, *>.configureAndroidComponents(){
//    val configs = FlavorConfig.disabledConfigs
//    val branches = configs.keys
//    beforeVariants { variant ->
//        val servers = branches.find { branch ->
//            variant.productFlavors.any {
//                it.first.contains(branch.dimensionName, true) &&
//                        it.second.contains(branch.flavorName, true)
//            }
//        }?.let {
//            configs[it]
//        }?: return@beforeVariants
//        val hasMatchingServer = servers.any { server ->
//            variant.productFlavors.any { (dimensionName, flavorName) ->
//                dimensionName.contains(server.dimensionName) &&
//                        flavorName.contains(server.flavorName)
//            }
//        }
//        variant.enable = !hasMatchingServer
//    }
}