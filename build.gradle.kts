plugins {
    kotlin("jvm") version "1.4.10"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "gregdev.gradle.docker"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    testImplementation("junit:junit:4.13")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
            from(components["java"])
        }
    }
}

pluginBundle {
    website = "https://github.com/gr3gdev/gradle-docker-plugin"
    vcsUrl = "https://github.com/gr3gdev/gradle-docker-plugin"
    tags = listOf("docker", "docker-compose", "run", "push")
}

gradlePlugin {
    plugins {
        create("dockerPlugin") {
            id = project.group as String
            displayName = "gregdev.gradle.docker"
            description = "Docker plugin for build, run and push"
            implementationClass = "gregdev.gradle.docker.DockerPlugin"
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
