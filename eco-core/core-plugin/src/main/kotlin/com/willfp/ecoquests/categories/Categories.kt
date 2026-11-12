package com.willfp.ecoquests.categories

import wiki.ecohub.config.Config
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.Registry

object Categories : NativeConfigCategory("categories", "category") {
    override val supportsSharing = false

    private val registry = Registry<Category>()

    override fun clear(plugin: EcoPlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(Category(plugin, id, config))
    }

    operator fun get(id: String?) = registry[id ?: ""]

    fun values(): Collection<Category> = registry.values()
}
