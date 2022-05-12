buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradlePlugin.android)
        classpath(libs.gradlePlugin.kotlin)
        classpath(libs.gradlePlugin.mavenPublish)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    group = property("GROUP") as String
    version = property("VERSION_NAME") as String
}
