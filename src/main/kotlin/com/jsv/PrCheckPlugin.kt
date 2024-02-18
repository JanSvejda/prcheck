package com.jsv

import org.gradle.api.*
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class PrCheckPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("prcheck", PrCheckTask::class.java)
    }
}

interface PrCheckRule : Named {
    fun getCondition(): Property<Boolean>
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
            if (rule.getCondition().get()) {
                println("Check task is running for ${rule.name}")
                println("Access token: ${accessToken.get()}")
                println("PR number: ${prNumber.get()}")
                println("Repository: ${repository.get()}")
                println("Server URL: ${serverUrl.get()}")
                println("Condition: ${rule.getCondition().get()}")
                println("Message: ${rule.getMessage().get()}")
            }
        }
    }
}
