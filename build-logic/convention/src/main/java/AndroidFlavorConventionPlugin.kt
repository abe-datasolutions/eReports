
import com.abedatasolutions.ereports.build.logic.convention.ProjectPlugin
import com.abedatasolutions.ereports.build.logic.convention.configuration.flavor.Server.Companion.configureServerFlavors
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidFlavorConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            when{
                pluginManager.hasPlugin(ProjectPlugin.AndroidApp.id) -> {
                    extensions.configure<ApplicationExtension> {
                        buildFeatures {
                            buildConfig = true
                        }
                        configureServerFlavors(this)
                    }
                }
                pluginManager.hasPlugin(ProjectPlugin.AndroidLib.id) -> {
                    extensions.configure<LibraryExtension> {
                        buildFeatures {
                            buildConfig = true
                        }
                        configureServerFlavors(this)
                    }
                }
                pluginManager.hasPlugin(ProjectPlugin.AndroidTest.id) -> {
                    extensions.configure<TestExtension> {
                        configureServerFlavors(this)
                    }
                }
            }
        }
    }
    companion object{
        const val PLUGIN_ID = "ereports.android.flavor"
    }
}