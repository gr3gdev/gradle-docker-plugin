package gregdev.gradle.docker.task

import gregdev.gradle.docker.ext.DockerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.RuntimeException

open class BuildImageTask : DefaultTask() {

    init {
        description = "Build a docker image"
    }

    @TaskAction
    fun exec() {
        val ext = project.extensions.getByType(DockerExtension::class.java)
        val dockerFile = File(project.projectDir, "Dockerfile")
        if (!dockerFile.exists()) {
            throw RuntimeException("Dockerfile not found : ${dockerFile.absolutePath}")
        }
        logger.info("docker build -t ${ext.imageName} .")
        project.exec {
            it.workingDir = project.projectDir
            it.commandLine("docker", "build", "-t", ext.imageName, ".")
        }
    }

}