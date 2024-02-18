package com.jsv

import org.gradle.api.*
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.net.URI

class PrCheckPlugin : Plugin<Project> {
    override fun apply(project: Project) {
//        val prcheck = project.prCheck()
//        project.tasks.register("prcheckAll", PrCheckTask::class.java) {
//            it.checks.set(prcheck)
//        }
        project.tasks.register("prcheck", PrCheckTask::class.java)
        project.tasks.register("dw", Download::class.java)
    }
}


abstract class Download : DefaultTask() {
    @get:Input
    abstract val uri: Property<String>

    @TaskAction
    fun run() {
        println("Downloading " + uri.get()) // Use the `uri` property
    }
}

abstract interface PrCheckRule2: Named {
    abstract fun getCondition(): Property<Boolean>
    abstract fun getMessage(): Property<String>
}

abstract class PrCheckTask : DefaultTask() {
    init {
        group = "verification"
        description = "Run all defined PR check tasks"
    }

    @get:Input abstract val accessToken: Property<String>
    @get:Input abstract val prNumber: Property<String>
    @get:Input abstract val repository: Property<String>
    @get:Input abstract val serverUrl: Property<String>
    @get:Input abstract val rules: NamedDomainObjectContainer<PrCheckRule2>

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
