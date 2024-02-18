package com.jsv

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path
import kotlin.test.assertContains


class PrCheckPluginTest {

    @TempDir
    lateinit var testProjectDir: File
    private lateinit var buildFile: File

    @BeforeEach
    fun setup() {
        buildFile = Path(testProjectDir.path, "build.gradle").toFile()
        buildFile.appendText(
            """
            plugins {
                id 'com.jsv.prcheck'
            }
        """
        )
    }

    @Test
    fun `plugin adds check task to project`() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.jsv.prcheck")

        assertNotNull(project.plugins.getPlugin(PrCheckPlugin::class.java))

        assertTrue(project.tasks.getByName("prcheck") is PrCheckTask)
    }

    @Test
    fun `can configure plugin`() {
        buildFile.appendText(
            """
            prcheck {
                accessToken 'accessToken'
                prNumber '2345'
                repository 'app-repo'
                serverUrl 'https://azdops.company.com/'
            }
            """
        )
        val result = GradleRunner.create().withProjectDir(testProjectDir)
            .withArguments("prcheck")
            .withPluginClasspath().build()

        assertContains(result.output, "Starting check task")
        assertEquals(TaskOutcome.SUCCESS, result.task(":prcheckAll")?.outcome)
    }

    @Test
    fun `can add custom check rules`() {
        buildFile.appendText(
            """
            prcheck {
                accessToken = 'accessToken'
                prNumber = '2345'
                repository = 'app-repo'
                serverUrl = 'https://azdops.company.com/'
                rules {
                    create("rule1") {
                        condition = true
                        message = "message1"
                    }
                    create("rule2") {
                        condition = false
                        message = "message2"
                    }
                    create("rule3") {
                        condition = true
                        message = "message3"
                    }
                }
            }
            """
        )
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("prcheck")
            .withPluginClasspath().build()

        assertContains(result.output, "Check task is running")
        assertContains(result.output, "message1")
        assertContains(result.output, "message3")
        assertFalse("message2" in result.output)
        assertEquals(TaskOutcome.SUCCESS, result.task(":prcheck")?.outcome)
    }

}