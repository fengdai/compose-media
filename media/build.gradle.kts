import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply(plugin = "com.vanniktech.maven.publish.base")

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    packagingOptions {
        resources.pickFirsts += "META-INF/AL2.0"
        resources.pickFirsts += "META-INF/LGPL2.1"
        resources.pickFirsts += "META-INF/*kotlin_module"
        resources.pickFirsts += "win32-x86-64/attach_hotspot_windows.dll"
        resources.pickFirsts += "win32-x86/attach_hotspot_windows.dll"
        resources.pickFirsts += "META-INF/licenses/ASM"
    }

    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.media3.common)
    implementation(libs.compose.foundation)

    androidTestImplementation(libs.media3.test.utils)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.compose.ui.test)
    debugImplementation(libs.compose.ui.test.manifest)
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
