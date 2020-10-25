package gregdev.gradle.docker.task

import gregdev.gradle.docker.ext.DockerExtension
import gregdev.gradle.docker.ext.RunMode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class StopTask : DefaultTask() {

    init {
        description = "Stop a docker container or docker-compose"
    }

    @TaskAction
    fun exec() {
        val ext = project.extensions.getByType(DockerExtension::class.java)
        if (ext.mode == RunMode.SIMPLE) {
            logger.info("docker stop ${project.name}_local")
            project.exec {
                it.commandLine("docker", "stop", "${project.name}_local")
            }
        } else if (ext.mode == RunMode.COMPOSE) {
            logger.info("docker-compose down")
            project.exec {
                it.workingDir = project.projectDir
                it.commandLine("docker-compose", "down")
            }
        }
    }

}