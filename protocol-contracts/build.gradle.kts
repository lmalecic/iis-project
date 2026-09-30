plugins {
    `java-library`
    id("com.google.protobuf") version "0.9.6"
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))

    api("com.google.protobuf:protobuf-java")
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-stub")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.35.1"
    }

    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.83.1"
        }
    }

    generateProtoTasks {
        all().configureEach {
            plugins {
                create("grpc")
            }
        }
    }
}