package gregdev.gradle.docker

import org.gradle.testkit.runner.GradleRunner
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File

class DockerPluginTest {

    private val projectName = "test-docker-plugin"
    private val testDir = File("build/$projectName")
    private val gradleRunner = GradleRunner.create()
        .withPluginClasspath()
        .withProjectDir(testDir)
    private var dockerConfig = """
        docker {
            dependsOn = 'jar'
            imageName = 'gr3gdev/$projectName'
        }
    """.trimIndent()

    private fun init(
        build: String,
        dockerFile: String,
        dockerCompose: String? = null
    ) {
        testDir.deleteRecursively()
        testDir.mkdirs()
        File(testDir, "build.gradle").writeText(build)
        File(testDir, "settings.gradle").writeText(
            """
            rootProject.name = '$projectName'
            """.trimIndent()
        )
        val srcPackage = File(testDir, "src/main/java/hello")
        srcPackage.mkdirs()
        File(srcPackage, "HelloWorld.java").writeText(
            """
            package hello;

            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Hello world !");
                }
            }
            """.trimIndent()
        )
        File(testDir, "Dockerfile").writeText(dockerFile)
        if (dockerCompose != null) {
            File(testDir, "docker-compose.yml").writeText(dockerCompose)
        }
    }

    @After
    fun purge() {
        val result = gradleRunner
            .withArguments(DockerPlugin.DELETE_DOCKER_IMAGE)
            .build()
        println(result.output)
        Assert.assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    private fun initJava(dockerCompose: String? = null) {
        init(
            """
            plugins {
                id 'java'
                id 'gregdev.gradle.docker'
            }
            $dockerConfig
            """.trimIndent(), """
            FROM openjdk:jre-slim
            COPY build/libs/$projectName.jar .
            ENTRYPOINT ["java", "-cp", "$projectName.jar", "hello.HelloWorld"]
            """.trimIndent(),
            dockerCompose
        )
    }

    private fun initApplication() {
        dockerConfig = """
            docker {
                dependsOn = 'installDist'
                imageName = 'gr3gdev/$projectName'
            }
        """.trimIndent()
        init(
            """
            plugins {
                id 'java'
                id 'application'
                id 'gregdev.gradle.docker'
            }
            $dockerConfig
            application {
                mainClassName = "hello.HelloWorld"
            }
            """.trimIndent(), """
            FROM openjdk:jre-slim
            COPY build/install/$projectName .
            RUN chmod +x bin/$projectName
            ENTRYPOINT ["./bin/$projectName"]
            """.trimIndent()
        )
    }

    @Test
    fun `dockerPlugin with java execute BUILD_DOCKER_IMAGE`() {
        initJava()
        val result = gradleRunner
            .withArguments(DockerPlugin.BUILD_DOCKER_IMAGE)
            .build()
        println(result.output)
        Assert.assertTrue(result.output.contains("> Task :buildDockerImage"))
        Assert.assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `dockerPlugin with application execute BUILD_DOCKER_IMAGE`() {
        initApplication()
        val result = gradleRunner
            .withArguments(DockerPlugin.BUILD_DOCKER_IMAGE)
            .build()
        println(result.output)
        Assert.assertTrue(result.output.contains("> Task :buildDockerImage"))
        Assert.assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `dockerPlugin with java execute DOCKER_RUN`() {
        initJava()
        val result = gradleRunner
            .withArguments(DockerPlugin.DOCKER_RUN)
            .build()
        println(result.output)
        Assert.assertTrue(result.output.contains("> Task :buildDockerImage"))
        Assert.assertTrue(result.output.contains("> Task :dockerRun"))
        Assert.assertTrue(result.output.contains("Hello world !"))
        Assert.assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `dockerPlugin with application execute DOCKER_RUN`() {
        initApplication()
        val result = gradleRunner
            .withArguments(DockerPlugin.DOCKER_RUN)
            .build()
        println(result.output)
        Assert.assertTrue(result.output.contains("> Task :buildDockerImage"))
        Assert.assertTrue(result.output.contains("> Task :dockerRun"))
        Assert.assertTrue(result.output.contains("Hello world !"))
        Assert.assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `dockerPlugin with docker-compose execute DOCKER_RUN`() {
        dockerConfig = """
            docker {
                dependsOn = 'jar'
                mode = gregdev.gradle.docker.ext.RunMode.COMPOSE
                imageName = 'gr3gdev/$projectName'
            }
        """.trimIndent()
        initJava("""
            version: "3.3"
            services:
              $projectName:
                build: .
        """.trimIndent())
        val result = gradleRunner
            .withArguments(DockerPlugin.DOCKER_RUN)
            .build()
        println(result.output)
        Assert.assertTrue(result.output.contains("> Task :buildDockerImage"))
        Assert.assertTrue(result.output.contains("> Task :dockerRun"))
        Assert.assertTrue(result.output.contains("Hello world !"))
        Assert.assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

}