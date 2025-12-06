plugins {
    `java-library`
    id("velocity-publish")
}

dependencies {
    implementation(libs.guava)
    implementation(libs.netty.handler)
    implementation(libs.lz4.java)
    implementation(libs.checker.qual)
}
