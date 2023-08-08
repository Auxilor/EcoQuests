package com.willfp.ecoquests.tasks

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory

object Tasks : ConfigCategory("task", "tasks") {
    private val registry = Registry<Task>()

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Task(plugin, id, config))
    }

    operator fun get(id: String?) = registry[id ?: ""]

    fun values(): Collection<Task> = registry.values()
}
