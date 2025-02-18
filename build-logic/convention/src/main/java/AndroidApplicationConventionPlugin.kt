
import com.abedatasolutions.ereports.build.logic.convention.ProjectDefaults
import com.abedatasolutions.ereports.build.logic.convention.ProjectPlugin
import com.abedatasolutions.ereports.build.logic.convention.configuration.configureAndroidComponents
import com.abedatasolutions.ereports.build.logic.convention.configuration.configureKotlinAndroid
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(ProjectPlugin.AndroidApp.id)
                apply(ProjectPlugin.KotlinAndroid.id)
            }

            extensions.configure<ApplicationAndroidComponentsExtension> {
                configureAndroidComponents()
            }

            extensions.configure<BaseAppModuleExtension> {
//                signingConfigs { //TODO
//                    create(ProjectDefaults.SIGNING_CONFIG_NAME) {
//                        storeFile = file("")
//                        storePassword = ""
//                        keyAlias = ""
//                        keyPassword = ""
//                    }
//                }

                defaultConfig {
                    applicationId = ProjectDefaults.APPLICATION_ID
                    targetSdk = ProjectDefaults.TARGET_SDK
                    versionName = ProjectDefaults.APP_VERSION_NAME

                    applicationVariants.all {
                        outputs.mapNotNull {
                            it as? BaseVariantOutputImpl
                        }.forEach { output ->
                            output.outputFileName = buildString {
                                append(ProjectDefaults.APK_VERSION_FILE_NAME)
                                mergedFlavor.versionName?.let {
                                    append('_')
                                    append(it)
                                }
                                versionName?.let {
                                    append('_')
                                    append(it)
                                }
                                buildType?.let {
                                    append('_')
                                    append(it.name)
                                }
                                append(".apk")
                            }
                        }
                    }
                }
                configureKotlinAndroid(this)
            }
        }
    }

    companion object{
        const val PLUGIN_ID = "ereports.android.application"
    }
}