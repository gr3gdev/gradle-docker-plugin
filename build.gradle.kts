plugins {
    kotlin("jvm") version "1.4.10"
    `java-gradle-plugin`
    `maven-publish`
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
