package com.willfp.ecoquests.quests

import com.willfp.ecoquests.plugin
import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.Registry

object Quests : ConfigCategory("quest", "quests") {
    override val supportsSharing = false

    private val registry = Registry<Quest>()

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
        MaxActiveQuests.invalidateAll()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Quest(plugin, id, config))
    }

    operator fun get(id: String?) = registry[id ?: ""]

    fun values(): Collection<Quest> = registry.values()

    fun getShownQuests(player: Player): List<Quest> {
        return values()
            .filter { it.showsInGui }
            .filter { quest ->
                quest.hasActive(player) ||
                    quest.alwaysInGUI && !quest.hasCompleted(player) && quest.isUnlockedFor(player) ||
                    quest.showsCompleted && quest.hasCompleted(player)
            }
    }

    fun getActiveQuests(player: Player): List<Quest> {
        return values()
            .filter { it.hasActive(player) }
    }

    fun getCompletedQuests(player: Player): List<Quest> {
        return values()
            .filter { it.hasCompleted(player) }
    }

    fun getMaxActiveQuests(player: Player): Int = MaxActiveQuests.get(player)

    fun hasReachedMaxActiveQuests(player: Player): Boolean {
        val max = getMaxActiveQuests(player)
        return max != -1 && getActiveQuests(player).size >= max
    }

    fun getShownCompletedQuests(player: Player): List<Quest> {
        return values()
            .filter { it.showsInGui }
            .filter { it.hasCompleted(player) }
    }
}

