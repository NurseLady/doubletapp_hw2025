plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    api("javax.inject:javax.inject:1")

    val dagger_ver = "2.56.2"
    implementation("com.google.dagger:dagger:$dagger_ver")
    ksp("com.google.dagger:dagger-compiler:$dagger_ver")
    implementation("com.squareup:javapoet:1.13.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    testImplementation("io.mockk:mockk:1.13.10")

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("com.google.truth:truth:1.4.2")
}