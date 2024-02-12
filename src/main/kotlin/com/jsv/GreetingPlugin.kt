package com.jsv

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

interface GreetingPluginExtension {
    val message: Property<String>
}

class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("com.jsv.prcheck", GreetingPluginExtension::class.java)
        project.tasks.register("hello", GreetingTask::class.java) {
            it.message.set(extension.message)
        }
    }
}

open class GreetingTask : DefaultTask() {

    @Input
    val message = project.objects.property(String::class.java)

    @TaskAction
    fun greet() {
        println("Hello and welcome, ${message.get()}")
    }
}


