package com.willfp.ecoquests.quests

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.ecoquests.plugin
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import org.bukkit.entity.Player

object Quests : ConfigCategory("quest", "quests") {
    override val supportsSharing = false

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

    fun getMaxActiveQuests(player: Player): Int {
        var permissionMax: Int? = null
        for (info in player.effectivePermissions) {
            if (!info.value) continue
            val value = info.permission
                .removePrefix("ecoquests.quests.max.")
                .takeIf { info.permission.startsWith("ecoquests.quests.max.") }
                ?.toIntOrNull() ?: continue
            if (permissionMax == null || value > permissionMax!!) permissionMax = value
        }
        return permissionMax ?: plugin.configYml.getInt("max-active-quests")
    }

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

