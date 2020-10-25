package gregdev.gradle.docker.task

import gregdev.gradle.docker.ext.DockerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class PushTask : DefaultTask() {

    init {
        description = "Push docker images with platforms specified"
    }

    @TaskAction
    fun exec() {
        val ext = project.extensions.getByType(DockerExtension::class.java)
        logger.info("docker buildx create --user --name build --driver-opt network=host")
        project.exec {
            it.commandLine("docker", "buildx", "create", "--use", "--name", "build", "--driver-opt", "network=host")
        }
        var publish = "${ext.imageName}:${project.version}"
        if (ext.distributionUrl != null) {
            publish = "${ext.distributionUrl}/${ext.imageName}:${project.version}"
        }
        logger.info("docker buildx build --push --platform ${ext.platforms} -t ${ext.imageName} -t $publish .")
        project.exec {
            it.workingDir = project.projectDir
            it.commandLine(
                "docker",
                "buildx",
                "build",
                "--push",
                "--platform",
                ext.platforms,
                "-t",
                ext.imageName,
                "-t",
                publish,
                "."
            )
        }
        logger.info("docker buildx rm build")
        project.exec {
            it.commandLine("docker", "buildx", "rm", "build")
        }
    }

}