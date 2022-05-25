import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply(plugin = "com.vanniktech.maven.publish.base")

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$buildDir/compose",
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$buildDir/compose"
        )
    }

    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.exoplayer.common)
    implementation(libs.compose.foundation)
}

afterEvaluate {
    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(SonatypeHost.DEFAULT)
        signAllPublications()
        pomFromGradleProperties()
    }
    extensions.configure<PublishingExtension> {
        publications.create<MavenPublication>("release") {
            from(components["release"])
            // https://github.com/vanniktech/gradle-maven-publish-plugin/issues/326
            val id = project.property("POM_ARTIFACT_ID").toString()
            artifactId = artifactId.replace(project.name, id)
        }
    }
}
