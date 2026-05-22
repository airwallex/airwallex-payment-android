plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("dokkaMarkdown") {
            id = "dokka-markdown"
            implementationClass = "DokkaMarkdownPlugin"
        }
    }
}

dependencies {
    compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0")
}
