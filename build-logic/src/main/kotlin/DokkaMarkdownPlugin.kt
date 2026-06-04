import org.jetbrains.dokka.gradle.formats.DokkaFormatPlugin
import org.jetbrains.dokka.gradle.internal.InternalDokkaGradlePluginApi

private const val DOKKA_VERSION = "2.0.0"

@OptIn(InternalDokkaGradlePluginApi::class)
abstract class DokkaMarkdownPlugin : DokkaFormatPlugin(formatName = "markdown") {
    override fun DokkaFormatPlugin.DokkaFormatPluginContext.configure() {
        with(project.dependencies) {
            dokkaPlugin("org.jetbrains.dokka:gfm-plugin:$DOKKA_VERSION")
        }
        formatDependencies.dokkaPublicationPluginClasspathApiOnly.dependencies.addLater(
            with(project.dependencies) { dokka("gfm-template-processing-plugin") }
        )
    }
}
