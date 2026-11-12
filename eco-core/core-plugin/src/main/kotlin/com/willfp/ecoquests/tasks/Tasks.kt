package com.willfp.ecoquests.tasks

import wiki.ecohub.config.Config
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.Registry

object Tasks : ConfigCategory("task", "tasks") {
    private val registry = Registry<TaskTemplate>()

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(TaskTemplate(id, config))
    }

    operator fun get(id: String?) = registry[id ?: ""]

    fun values(): Collection<TaskTemplate> = registry.values()
}
