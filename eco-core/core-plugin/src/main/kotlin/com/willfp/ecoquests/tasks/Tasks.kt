package com.willfp.ecoquests.tasks

import wiki.ecohub.config.Config
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.Registry

object Tasks : NativeConfigCategory("tasks", "task") {
    private val registry = Registry<TaskTemplate>()

    override fun clear(plugin: EcoPlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(TaskTemplate(id, config))
    }

    operator fun get(id: String?) = registry[id ?: ""]

    fun values(): Collection<TaskTemplate> = registry.values()
}
