package com.jsv

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

interface PrCheckPluginExtension {
    val message: Property<String>
    val condition: Property<Boolean>
    val setStatus: Property<String>
    val addTags: ListProperty<String>
}

class PrCheckPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("com.jsv.prcheck", PrCheckPluginExtension::class.java)
        project.tasks.register("check", CheckTask::class.java) {
            it.message.set(extension.message)
            it.condition.set(extension.condition)
            it.setStatus.set(extension.setStatus)
            it.addTags.set(extension.addTags)
        }
    }
}

open class CheckTask : DefaultTask() {

    @Input
    val message = project.objects.property(String::class.java)
    @Input
    val condition = project.objects.property(Boolean::class.java)
    @Input
    val setStatus = project.objects.property(String::class.java)
    @Input
    val addTags = project.objects.listProperty(String::class.java)

    @TaskAction
    fun check() {

    }
}
