import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij.platform") version "2.2.1"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.15.0")

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
    groups = emptyList()
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
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
        changeNotes = changelog.getLatest().toHTML()

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }
    }

//    publishing {
//        token = System.getenv("PUBLISH_TOKEN")
//        channels = properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.')
//    }

    pluginVerification {

        ides {
            val platformVersion = properties("platformVersion")

            ide(IntelliJPlatformType.PyCharmCommunity, platformVersion)
            recommended()

//            select {
//                types = listOf(IntelliJPlatformType.PyCharmCommunity)
//                sinceBuild = properties("pluginSinceBuild")
//                untilBuild = properties("pluginUntilBuild")
//            }
        }
    }
}

tasks {
    // Set the compatibility versions to 17
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<Detekt> {
        jvmTarget = "17"
    }
}
