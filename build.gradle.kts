import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.intellij.platform.gradle.tasks.PrepareSandboxTask

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij.platform") version "2.5.0"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.2.1"

    kotlin("jvm") version "2.1.10"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

kotlin {
    jvmToolchain(21)
}

// Configure project's dependencies
repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

dependencies {
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j.debug:0.24.0")
    implementation("org.apache.commons:commons-text:1.13.0")

    intellijPlatform {
        val platformVersion = properties("platformVersion")

        pycharmCommunity(platformVersion)
        jetbrainsRuntime()

        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        pluginVerifier()
    }
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version = properties("pluginVersion")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
    path.set(file("CHANGELOG.md").canonicalPath)
    itemPrefix.set("-")
    lineSeparator.set("\n")
    keepUnreleasedSection.set(false)
}

intellijPlatform {

    pluginConfiguration {
        id = properties("pluginGroup")
        name = properties("pluginName")
        version = properties("pluginVersion")
        description = File("./README.md").readText().lines().run {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            if (!containsAll(listOf(start, end))) {
                throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
            }
            subList(indexOf(start) + 1, indexOf(end))
        }.joinToString("\n").run { markdownToHTML(this) }
        changeNotes = changelog.renderItem(changelog.getLatest(), Changelog.OutputType.HTML)

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }
    }

    signing {
        certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.')
    }

    pluginVerification {
        ides {
            recommended()
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = properties("pluginSinceBuild")
                untilBuild = properties("pluginUntilBuild")
            }
        }
    }
}

tasks {
    // Set the compatibility versions to 21
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    withType<PrepareSandboxTask> {
        from(layout.projectDirectory) {
            include("bundled/**/*")
            exclude("**/*.iml")
            exclude("**/bin")
            exclude("**/__pycache__")
            into(pluginName.map { "$it/data" })
        }
    }

    patchPluginXml {
        changeNotes.set(provider {
            changelog.renderItem(
                changelog.getLatest().withHeader(true).withEmptySections(false), Changelog.OutputType.HTML
            )
        })
    }
}
