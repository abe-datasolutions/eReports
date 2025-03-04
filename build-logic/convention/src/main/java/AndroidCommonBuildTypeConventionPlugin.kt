import com.abedatasolutions.ereports.build.logic.convention.ProjectPlugin
import com.abedatasolutions.ereports.build.logic.convention.configuration.BuildTypeConfiguration
import com.abedatasolutions.ereports.build.logic.convention.configuration.BuildTypeConfiguration.Companion.configureBuildTypes
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidCommonBuildTypeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            when{
                pluginManager.hasPlugin(ProjectPlugin.AndroidApp.id) -> {
                    extensions.configure<ApplicationExtension> {
                        buildFeatures {
                            buildConfig = true
                        }
                        configureBuildTypes(this, BuildTypeConfiguration.common)
                    }
                }
                pluginManager.hasPlugin(ProjectPlugin.AndroidLib.id) -> {
                    extensions.configure<LibraryExtension> {
                        buildFeatures {
                            buildConfig = true
                        }
                        configureBuildTypes(this, BuildTypeConfiguration.common)
                    }
                }
                pluginManager.hasPlugin(ProjectPlugin.AndroidTest.id) -> {
                    extensions.configure<TestExtension> {
                        configureBuildTypes(this, BuildTypeConfiguration.common)
                    }
                }
            }
        }
    }
    companion object{
        const val PLUGIN_ID = "ereports.android.build.type.common"
    }
}