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
                accessToken = 'accessToken'
                prNumber = '2345'
                repository = 'app-repo'
                serverUrl = 'https://azdops.company.com/'
            }
            """
        )
        val result = GradleRunner.create().withProjectDir(testProjectDir)
            .withArguments("prcheck")
            .withPluginClasspath().build()

        assertContains(result.output, "Starting check task")
        assertEquals(TaskOutcome.SUCCESS, result.task(":prcheck")?.outcome)
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
                        enabled = true
                        message = "message1"
                    }
                    create("rule2") {
                        enabled = false
                        message = "message2"
                    }
                    create("rule3") {
                        enabled = true
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


    @Test
    fun `can use conditions on changed files`() {
        buildFile.appendText( // this is a gradle groovy file executed by the test framework
            """
            // create file named file1.txt and file2.txt
            file("file1.txt").write("content1")
            file("file2.txt").write("content2")
            
            // create directory named dir1 and dir2
            file("dir1").mkdirs()
            file("dir2").mkdirs()
            
            // create file named file1.txt and file2.txt in dir1 and dir2
            file("dir1/file1.txt").write("content1")
            file("dir1/file2.txt").write("content2")
            file("dir2/file1.txt").write("content1")
            file("dir2/file2.txt").write("content2")
               
            prcheck {
                accessToken = 'accessToken'
                prNumber = '2345'
                repository = 'app-repo'
                serverUrl = 'https://azdops.company.com/'
                rules {
                    create("rule1") {
                        enabled = true
                        message = "message1"
                        watched = files("file1.txt", "file2.txt")
                    }
                    create("rule2") {
                        enabled = true
                        message = "message2"
                        watched = files("dir1", "dir2")
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
        assertContains(result.output, "Checking file file1.txt...")
        assertContains(result.output, "Checking file file2.txt...")

        assertContains(result.output, "message2")
        assertContains(result.output, "Checking directory dir1...")
        assertContains(result.output, "Checking file dir1/file1.txt...")
        assertContains(result.output, "Checking file dir1/file2.txt...")
        assertContains(result.output, "Checking directory dir2...")
        assertContains(result.output, "Checking file dir2/file1.txt...")
        assertContains(result.output, "Checking file dir2/file2.txt...")
        assertEquals(TaskOutcome.SUCCESS, result.task(":prcheck")?.outcome)
    }

}