
import com.abedatasolutions.ereports.build.logic.convention.ProjectDefaults
import com.abedatasolutions.ereports.build.logic.convention.ProjectPlugin
import com.abedatasolutions.ereports.build.logic.convention.configuration.configureAndroidComponents
import com.abedatasolutions.ereports.build.logic.convention.configuration.configureKotlinAndroid
import com.abedatasolutions.ereports.build.logic.convention.libs
import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidTestConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(ProjectPlugin.AndroidTest.id)
                apply(ProjectPlugin.KotlinAndroid.id)
            }
            extensions.configure<TestAndroidComponentsExtension> {
                configureAndroidComponents()
            }
            extensions.configure<TestExtension> {
                defaultConfig {
                    targetSdk = ProjectDefaults.TARGET_SDK
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                configureKotlinAndroid(this)
            }
            dependencies {
                "implementation"(libs.findLibrary("androidx.junit").get())
                "implementation"(libs.findLibrary("androidx.espresso.core").get())
            }
        }
    }

    companion object{
        const val PLUGIN_ID = "ereports.android.test"
    }
}