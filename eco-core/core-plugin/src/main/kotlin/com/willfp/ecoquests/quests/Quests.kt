package com.willfp.ecoquests.quests

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import org.bukkit.entity.Player

object Quests : ConfigCategory("quest", "quests") {
    private val registry = Registry<Quest>()

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Quest(plugin, id, config))
    }

    operator fun get(id: String?) = registry[id ?: ""]

    fun values(): Collection<Quest> = registry.values()

    fun getShownQuests(player: Player): List<Quest> {
        return values()
            .filter { it.showsInGui }
            .filter { it.hasActive(player) || (it.alwaysInGUI && !it.hasCompleted(player)) }
    }

    fun getActiveQuests(player: Player): List<Quest> {
        return values()
            .filter { it.hasActive(player) }
    }

    fun getCompletedQuests(player: Player): List<Quest> {
        return values()
            .filter { it.hasCompleted(player) }
    }

    fun getShownCompletedQuests(player: Player): List<Quest> {
        return values()
            .filter { it.showsInGui }
            .filter { it.hasCompleted(player) }
    }
}

