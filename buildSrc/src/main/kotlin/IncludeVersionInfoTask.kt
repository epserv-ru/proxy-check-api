import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.path
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

abstract class IncludeVersionInfoTask : DefaultTask() {
    @get:Input
    val implementationName = project.objects.property<String>().convention(project.name)

    @get:Input
    val implementationVersion = project.objects.property<String>().convention(project.version.toString())

    @get:Input
    val implementationVendor = project.objects.property<String>().convention("ElectroPlay Development Team")

    @get:Input
    val specificationName = project.objects.property<String>().convention("proxycheck.io")

    @get:Input
    val specificationVersion = project.objects.property<String>()

    @get:Input
    val specificationVendor = project.objects.property<String>().convention("https://proxycheck.io/api/")

    @get:Input
    val gitBranch = project.objects
        .property<String>()
        .convention(project.git().exec(project.providers, "rev-parse", "--abbrev-ref", "HEAD").map { it.trim() })

    @get:Input
    val gitCommit = project.objects
        .property<String>()
        .convention(project.git().exec(project.providers, "rev-parse", "--short=7", "HEAD").map { it.trim() })

    @get:Input
    val gitTimestamp = project.objects
        .property<String>()
        .convention(gitCommit.flatMap { gitCommit -> project.git().exec(project.providers, "show", "-s", "--format=%cI", gitCommit) }.map { it.trim() })

    @get:Input
    val contactWebsite = project.objects.property<String>().convention("https://epserv.ru/")

    @get:Input
    val contactEmail = project.objects.property<String>().convention("admin@epserv.ru")

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun includeVersionInfo() {
        val outputPath = outputFile.get().path
        outputPath.createParentDirectories()

        val versionInfo = buildJsonObject {
            put("implementation_name", implementationName.get())
            put("implementation_version", implementationVersion.get())
            put("implementation_vendor", implementationVendor.get())
            put("specification_name", specificationName.get())
            put("specification_version", specificationVersion.get())
            put("specification_vendor", specificationVendor.get())
            put("git_branch", gitBranch.get())
            put("git_commit", gitCommit.get())
            put("git_timestamp", gitTimestamp.get())
            put("contact_website", contactWebsite.get())
            put("contact_email", contactEmail.get())
        }

        outputPath.writeText(Json.encodeToString(versionInfo))
    }

    companion object {
        fun Project.git(): Git = Git(rootProject.layout.projectDirectory.path)
    }
}
