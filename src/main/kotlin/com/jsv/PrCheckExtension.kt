package com.jsv

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("Unused")
open class PrCheckExtension @Inject constructor(objects: ObjectFactory) {

    internal val accessToken: Property<String> = objects.property(String::class.java)
    internal val prNumber: Property<String> = objects.property(String::class.java)
    internal val repository: Property<String> = objects.property(String::class.java)
    internal val serverUrl: Property<String> = objects.property(String::class.java)
    companion object {
        internal fun Project.prCheck(): PrCheckExtension = extensions.create("prcheck", PrCheckExtension::class.java)
    }

    val rules: NamedDomainObjectContainer<PrCheckRule> = objects.domainObjectContainer(PrCheckRule::class.java)

    fun rules(action: Action<NamedDomainObjectContainer<PrCheckRule>>) {
        action.execute(rules)
    }

    fun rules(config: Closure<*>) {
        this.rules.configure(config)
    }

    fun accessToken(accessToken: String) {
        this.accessToken.set(accessToken)
        this.accessToken.disallowChanges()
    }

    fun prNumber(prNumber: String) {
        this.prNumber.set(prNumber)
        this.prNumber.disallowChanges()
    }

    fun repository(repository: String) {
        this.repository.set(repository)
        this.repository.disallowChanges()
    }

    fun serverUrl(serverUrl: String) {
        this.serverUrl.set(serverUrl)
        this.serverUrl.disallowChanges()
    }
}

@Suppress("Unused")
open class PrCheckRule @Inject constructor(private val name: String, objects: ObjectFactory) : Named {
    override fun getName(): String = name

    internal val condition: Property<Boolean> = objects.property(Boolean::class.java)
    internal val message: Property<String> = objects.property(String::class.java)

    fun condition(condition: Boolean) {
        this.condition.set(condition)
        this.condition.disallowChanges()
    }

    fun message(message: String) {
        this.message.set(message)
        this.message.disallowChanges()
    }
}
