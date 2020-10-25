# Docker plugin for build, run and push

## Dependency
```
plugins {
    id("gregdev.gradle.docker") version "1.0.0"
}
```

## Configuration
```
docker {
    imageName = "account/image"
    platforms = "linux/amd64,linux/arm64,linux/arm/v6,linux/arm/v7"
}
```
- imageName : the name of the image
- platforms : list of platforms used
- mode : SIMPLE for use `docker run`, COMPOSE for use `docker-compose up -d`
- args : list of args, for example `docker run -p 8888:8888 -p 8080:8080 account/image`
- distributionUrl : URL to publish the docker image
- dependsOn : task to execute before build image

## Execution
### Build image
`gradle :buildDockerImage`
### Delete image
`gradle :deleteDockerImage`
### Run
`gradle :dockerRun`
### Stop
`gradle :dockerStop`
### Push
`gradle :dockerPush`