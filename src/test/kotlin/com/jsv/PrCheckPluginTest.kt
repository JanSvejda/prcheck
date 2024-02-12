package com.jsv

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class PrCheckPluginTest {

    @Test
    fun `plugin adds check task to project`() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.jsv.prcheck")

        assertNotNull(project.plugins.getPlugin(PrCheckPlugin::class.java))

        assertTrue(project.tasks.getByName("check") is CheckTask)
    }
}