package com.willfp.ecoquests.quests

import com.willfp.ecoquests.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object MaxActiveQuests : Listener {
    private const val PERMISSION_PREFIX = "ecoquests.quests.max."

    private val cache = ConcurrentHashMap<UUID, CachedMax>()

    /**
     * Returns an object that is replaced whenever the player's permissions change,
     * or null when no permissions plugin provides one, which disables the cache.
     */
    @Volatile
    internal var permissionsVersion: ((Player) -> Any)? = null

    fun get(player: Player): Int {
        val version = permissionsVersion?.invoke(player) ?: return calculate(player)

        val cached = cache[player.uniqueId]
        if (cached != null && cached.version === version) {
            return cached.max
        }

        return calculate(player).also { cache[player.uniqueId] = CachedMax(version, it) }
    }

    fun invalidateAll() {
        cache.clear()
    }

    private fun calculate(player: Player): Int {
        var permissionMax: Int? = null
        for (info in player.effectivePermissions) {
            if (!info.value) continue
            val value = info.permission
                .removePrefix(PERMISSION_PREFIX)
                .takeIf { info.permission.startsWith(PERMISSION_PREFIX) }
                ?.toIntOrNull() ?: continue
            if (permissionMax == null || value > (permissionMax ?: 0)) permissionMax = value
        }
        return permissionMax ?: plugin.configYml.getInt("max-active-quests")
    }

    @EventHandler
    fun handle(event: PlayerQuitEvent) {
        cache.remove(event.player.uniqueId)
    }

    private data class CachedMax(val version: Any, val max: Int)
}
