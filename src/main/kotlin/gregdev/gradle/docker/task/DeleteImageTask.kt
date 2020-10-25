package gregdev.gradle.docker.task

import gregdev.gradle.docker.ext.DockerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DeleteImageTask : DefaultTask() {

    init {
        description = "Delete a docker image"
    }

    @TaskAction
    fun exec() {
        val ext = project.extensions.getByType(DockerExtension::class.java)
        logger.info("docker image rm ${ext.imageName}")
        project.exec {
            it.workingDir = project.projectDir
            it.commandLine("docker", "image", "rm", ext.imageName)
        }
    }

}