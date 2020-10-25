package gregdev.gradle.docker.task

import gregdev.gradle.docker.ext.DockerExtension
import gregdev.gradle.docker.ext.RunMode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class RunTask : DefaultTask() {

    init {
        description = "Run a docker container or docker-compose"
    }

    @TaskAction
    fun exec() {
        val ext = project.extensions.getByType(DockerExtension::class.java)
        if (ext.mode == RunMode.SIMPLE) {
            val args = ArrayList<String>(
                listOf(
                    "docker", "run", "--rm", "--name", "${project.name}_local"
                )
            )
            if (ext.args != null) {
                args.addAll(ext.args!!)
            }
            args.add(ext.imageName)
            logger.info(args.joinToString(" "))
            project.exec {
                it.commandLine(args)
            }
        } else if (ext.mode == RunMode.COMPOSE) {
            if (ext.args != null) {
                logger.warn("args must not be assigned with mode COMPOSE")
            }
            val dockerCompose = File(project.projectDir, "docker-compose.yml")
            if (!dockerCompose.exists()) {
                throw RuntimeException("dockerCompose must be exists with mode COMPOSE")
            }
            logger.info("docker-compose up -d")
            project.exec {
                it.workingDir = project.projectDir
                it.commandLine("docker-compose", "up", "-d")
            }
            project.exec {
                it.workingDir = project.projectDir
                it.commandLine("docker-compose", "logs", "-f")
            }
        }
    }

}