package gregdev.gradle.docker.ext

open class DockerExtension() {

    lateinit var imageName: String
    var dependsOn: String? = null
    var platforms: String = "linux/amd64"
    var distributionUrl: String? = null
    var mode = RunMode.SIMPLE
    var args: List<String>? = null

}