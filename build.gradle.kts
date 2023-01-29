plugins {
    id("com.android.library").version("7.3.1")
    kotlin("multiplatform").version("1.8.0")
    kotlin("plugin.serialization").version("1.7.20")
    id("maven-publish")
}

group = "com.nekzabirov.socketIO"
version = "1.0.0"

kotlin {
    android {
        publishLibraryVariants("release")
    }

    iosArm64 {
        val socketIOFramework = File(project.rootDir, "src/libs/Ios/socket_IO/ios-arm64").absolutePath
        val rocketFramework = File(project.rootDir, "src/libs/Ios/SocketRocket/ios-arm64").absolutePath

        val socketFrameworkCompilerLinkerOpts = listOf("-framework", "socket_IO", "-F${socketIOFramework}")
        val rocketFrameworkCompilerLinkerOpts = listOf("-framework", "socket_IO", "-F${rocketFramework}")

        compilations.getByName("main") {
            val socketIO by cinterops.creating {
                val defFile = File(project.rootDir, "src/libs/Ios/socketIO.def")

                defFile(defFile.absolutePath)

                compilerOpts(socketFrameworkCompilerLinkerOpts)
                compilerOpts(rocketFrameworkCompilerLinkerOpts)
            }
        }

        binaries.framework {
            isStatic = true

            linkerOpts(socketFrameworkCompilerLinkerOpts)
            linkerOpts(rocketFrameworkCompilerLinkerOpts)
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("co.touchlab:kermit:1.2.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(fileTree(mapOf(
                            "dir" to "src/libs/android",
                            "include" to listOf("*.jar")
                        )))
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        val iosArm64Main by getting
        val iosArm64Test by getting
        val iosMain by creating {
            dependsOn(commonMain)

            iosArm64Main.dependsOn(this)
        }
        val iosTest by creating {
            dependsOn(commonTest)
            iosArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.nekzabirov.socketIO"
    compileSdk = 32
    defaultConfig {
        minSdk = 21
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}
