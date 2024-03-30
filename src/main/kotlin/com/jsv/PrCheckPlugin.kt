package com.jsv

import org.gradle.api.*
import org.gradle.api.logging.Logger
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
        val log = project.logger
        log.debug("Starting check task...")
        log.debug("Access token: ${if (accessToken.get().isNotEmpty()) "****" else "empty"}")
        log.debug("PR number: ${prNumber.get()}")
        log.debug("Repository: ${repository.get()}")
        log.debug("Server URL: ${serverUrl.get()}")
        val ruleChecker = RuleCheckerFactory.create(project)
        rules.filter { it.getEnabled().get() }.forEach { rule ->
            ruleChecker.check(rule)
        }
    }
}



interface RuleChecker {
    fun check(rule: PrCheckRule)
}

class RuleCheckerFactory {
    companion object {
        fun create(project: Project): RuleChecker {
            val log = project.logger
            return if (project.hasProperty("git")) {
                GitPrCheckRule(project)
            } else {
                log.warn("Not a Git project, the check will only validate the existence of files.")
                DefaultPrCheckRule(project)
            }
        }
    }
}

class GitPrCheckRule(project: Project) : RuleChecker {
    val log = project.logger

    override fun check(rule: PrCheckRule) {
        // check the diff between the current branch and the target branch of the PR
        log.debug("Check task is running for ${rule.name}")
    }
}

class DefaultPrCheckRule(project: Project) : RuleChecker {
    private val log: Logger = project.logger

    override fun check(rule: PrCheckRule) {
        // check if the files or directories exist in the project directory
        log.debug("Checking rule ${rule.name}")

        val found = rule.getWatched().get().find { it.exists() }
        if (found == null) {
            log.debug("No files found for rule ${rule.name}")
            return
        }
        log.debug("Found ${found.name}")
        log.debug(rule.getMessage().get())
    }
}
