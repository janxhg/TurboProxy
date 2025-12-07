import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.withType
import java.io.ByteArrayOutputStream

// This interface is needed as a workaround to get an instance of ExecOperations
interface Injected {
    @get:Inject
    val execOps: ExecOperations
}

val currentShortRevision = ByteArrayOutputStream().use {
    val execOps = objects.newInstance<Injected>().execOps
    execOps.exec {
        executable = "git"
        args = listOf("rev-parse", "HEAD")
        standardOutput = it
    }
    it.toString().trim().substring(0, 8)
}

tasks.withType<Jar> {
    manifest {
        val buildNumber = System.getenv("BUILD_NUMBER")
        val turboHumanVersion: String =
            if (project.version.toString().endsWith("-SNAPSHOT")) {
                if (buildNumber == null) {
                    "${project.version} (git-$currentShortRevision)"
                } else {
                    "${project.version} (git-$currentShortRevision-b$buildNumber)"
                }
            } else {
                archiveVersion.get()
            }
        attributes["Implementation-Version"] = turboHumanVersion
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Vendor"] = "TurboPowered"
    }
}
