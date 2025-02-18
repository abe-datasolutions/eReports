
import com.abedatasolutions.ereports.build.logic.convention.ProjectPlugin
import com.abedatasolutions.ereports.build.logic.convention.configuration.configureAndroidComponents
import com.abedatasolutions.ereports.build.logic.convention.configuration.configureKotlinAndroid
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(ProjectPlugin.AndroidLib.id)
                apply(ProjectPlugin.KotlinAndroid.id)
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configureAndroidComponents()
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                configureKotlinAndroid(this)
            }
        }
    }

    companion object{
        const val PLUGIN_ID = "ereports.android.library"
    }
}