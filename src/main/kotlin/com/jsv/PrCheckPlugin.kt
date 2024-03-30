package com.jsv

import org.gradle.api.*
import org.gradle.api.file.FileTree
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

class PrCheckPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("prcheck", PrCheckTask::class.java)
    }
}

interface PrCheckRule : Named {
    fun getEnabled(): Property<Boolean>
    fun getWatched(): ListProperty<File>
    fun getMessage(): Property<String>
}

abstract class PrCheckTask : DefaultTask() {
    init {
        group = "verification"
        description = "Run all defined PR check tasks"
    }

    @get:Input
    abstract val accessToken: Property<String>
    @get:Input
    abstract val prNumber: Property<String>
    @get:Input
    abstract val repository: Property<String>
    @get:Input
    abstract val serverUrl: Property<String>
    @get:Input
    abstract val rules: NamedDomainObjectContainer<PrCheckRule>

    @TaskAction
    fun check() {
        println("Starting check task...")
        for (rule in rules) {
            if (rule.getEnabled().get()) {
                println("Check task is running for ${rule.name}")
                println("Access token: ${accessToken.get()}")
                println("PR number: ${prNumber.get()}")
                println("Repository: ${repository.get()}")
                println("Server URL: ${serverUrl.get()}")
                println("Enabled: ${rule.getEnabled().get()}")
                println("Message: ${rule.getMessage().get()}")
            }
            for (file in rule.getWatched().get()) {
                if (file.exists() && file.isFile) {
                    println("Checking file ${file.name}...")
                } else if (file.exists() && file.isDirectory) {
                    println("Checking directory ${file.name}...")
                    val fileTree: FileTree = project.fileTree(file)
                    fileTree.forEach { fileInTree ->
                        if (fileInTree.isFile) {
                            println("Checking file ${file.name}/${fileInTree.name}...")
                        }
                    }
                }
            }
        }
    }
}
