import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("org.jetbrains.compose") version "0.0.0-unmerged-build21"
}

group = "top.gtf35.apkhelper"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.zeroturnaround:zt-exec:1.12")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "top.gtf35.apkhelper.MainScreenKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ApkInstallHelper"
            version = "1.0.0"
            description = "Help you install apk"
            copyright = "© 2020 gtf35. All rights reserved."
            vendor = "NekoProject"
            macOS {
                iconFile.set(project.file("art/icon.icns"))
            }
            windows {
                iconFile.set(project.file("art/icon.ico"))
                shortcut = true
                dirChooser = true
            }
            linux {
                iconFile.set(project.file("art/icon.png"))
                shortcut = true
            }
        }
    }
}
