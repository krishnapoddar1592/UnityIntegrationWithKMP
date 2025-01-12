import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    ios()
    iosSimulatorArm64()
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "shared"
            isStatic = true
        }

        target.compilations.getByName("main") {
            cinterops {
                create("unity") {
                    defFile(project.file("src/nativeInterop/cinterop/unity.def"))
                    packageName("com.chatsdk.unitydemo")

                    // Add headers directory
                    headers(project.file("src/nativeInterop/headers"))

                    // Specify compiler options
                    compilerOpts.add("-F${project.projectDir}/../../iosApp/Frameworks")
                    compilerOpts.add("-I${project.projectDir}/../../iosApp/Frameworks/UnityFramework.framework/Headers")
                    compilerOpts.add("-I${project.projectDir}/src/nativeInterop/headers")
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }
        androidMain.dependencies {

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val iosMain by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
    namespace = "com.chatsdk.unitydemo"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

