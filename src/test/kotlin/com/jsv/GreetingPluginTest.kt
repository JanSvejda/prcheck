package com.jsv

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class GreetingPluginTest {

    @Test
    fun greeterPluginAddsGreetingTaskToProject() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.jsv.prcheck")

        assertNotNull(project.plugins.getPlugin(GreetingPlugin::class.java))

        assertTrue(project.tasks.getByName("hello") is GreetingTask)
    }
}