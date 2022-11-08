package io.getstream.video.android.commands.rpc.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import io.getstream.video.android.commands.rpc.task.GenerateRPCServiceTask

private const val CONFIG_CLOJURE_NAME = "generateRPCServices"
private const val COMMAND_NAME = "generateServices"

class GenerateRPCServicePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension: GenerateRPCServiceExtension =
            project.extensions.create(CONFIG_CLOJURE_NAME, GenerateRPCServiceExtension::class.java)

        project.tasks.create(COMMAND_NAME, GenerateRPCServiceTask::class.java) {
            extension.outputDir = "${project.buildDir}/generated/source/services/io/getstream/video/android/api"
            this.config = extension
        }

        project.afterEvaluate {
            this.tasks.getByName("generateProtos").finalizedBy(project.tasks.getByName(COMMAND_NAME))
            this.tasks.getByName("generateDebugProtos").finalizedBy(project.tasks.getByName(COMMAND_NAME))
            this.tasks.getByName("generateReleaseProtos").finalizedBy(project.tasks.getByName(COMMAND_NAME))
        }
    }
}