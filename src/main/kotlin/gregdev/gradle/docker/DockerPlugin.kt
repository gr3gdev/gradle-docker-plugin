package gregdev.gradle.docker

import gregdev.gradle.docker.ext.DockerExtension
import gregdev.gradle.docker.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerPlugin : Plugin<Project> {

    companion object {
        private const val GROUP = "docker"
        const val BUILD_DOCKER_IMAGE = "buildDockerImage"
        const val DELETE_DOCKER_IMAGE = "deleteDockerImage"
        const val DOCKER_RUN = "dockerRun"
        const val DOCKER_STOP = "dockerStop"
        const val DOCKER_PUSH = "dockerPush"
    }

    override fun apply(project: Project) {
        project.extensions.create("docker", DockerExtension::class.java)
        val ext = project.extensions.getByType(DockerExtension::class.java)

        // Always rerun tasks
        project.tasks.forEach {
            it.outputs.upToDateWhen {
                false
            }
        }
        project.tasks.register(BUILD_DOCKER_IMAGE, BuildImageTask::class.java) {
            if (ext.dependsOn != null) {
                it.dependsOn(ext.dependsOn)
            }
            it.group = GROUP
        }
        project.tasks.register(DELETE_DOCKER_IMAGE, DeleteImageTask::class.java) {
            it.group = GROUP
        }
        project.tasks.register(DOCKER_RUN, RunTask::class.java) {
            it.group = GROUP
            it.dependsOn(BUILD_DOCKER_IMAGE)
        }
        project.tasks.register(DOCKER_STOP, StopTask::class.java) {
            it.group = GROUP
        }
        project.tasks.register(DOCKER_PUSH, PushTask::class.java) {
            it.group = GROUP
        }
    }

}