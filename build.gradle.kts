import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")  version "1.4.3"
}

group = "moe.nyamori"
version = extra["app.version"]!!

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.macos_arm64)
                implementation("org.apache.commons:commons-imaging:1.0-alpha3")
                implementation("commons-codec:commons-codec:1.15")
                implementation("org.apache.commons:commons-lang3:3.12.0")
//                implementation("io.ktor:ktor-server-freemarker:2.3.3")
                implementation("org.freemarker:freemarker:2.3.32")
                implementation("org.jsoup:jsoup:1.16.1")

                // Loggin
                implementation("org.slf4j:slf4j-api:2.0.7")
                implementation("org.slf4j:jul-to-slf4j:2.0.7")
                implementation("ch.qos.logback:logback-classic:1.4.7")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "QQ Mht2html"

            packageVersion = project.version as String
            description = "QQ Mht2html"
            copyright = "(C) 2022-2023 gyakkun. Some rights reserved."
            vendor = "gyakkun"
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            // includeAllModules = true
            modules(
                "java.logging",
                "java.naming",
                "jdk.crypto.ec",
                "java.base",
                "java.desktop",
            )

            val iconsRoot = project.file("src/jvmMain/resources/drawables")

            linux {
                iconFile.set(iconsRoot.resolve("qq-mht2html.png"))
            }

            windows {
                iconFile.set(iconsRoot.resolve("qq-mht2html.ico"))
                // Wondering what the heck is this? See : https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "92D39676-2715-4362-82D2-BE4A1923D4AC"
                menuGroup = packageName
                // perUserInstall = true
            }

            macOS {
                iconFile.set(iconsRoot.resolve("qq-mht2html.icns"))
            }

        }
    }
}

tasks.register<JavaExec>("runCli") {
    group = "Application"
    description = "Runs the Cli.kt file"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("CliKt") // replace with your package and class name
}

