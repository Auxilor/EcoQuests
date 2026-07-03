package com.willfp.ecoquests.categories

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.toNiceString
import com.willfp.ecoquests.api.event.PlayerCategoryCompleteEvent
import com.willfp.ecoquests.quests.Quests
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class Category(
    private val plugin: EcoPlugin,
    override val id: String,
    val config: Config
) : KRegistrable {
    val name = config.getFormattedString("name")

    private val effects = Effects.compileChain(
        config.getSubsections("effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "category $id effects")
    )

    init {
        PlayerlessPlaceholder(plugin, "category_${id}_name") {
            name
        }.register()

        PlayerPlaceholder(plugin, "category_${id}_complete") { player ->
            isCompleted(player).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "category_${id}_quests_completed") { player ->
            getCompletedCount(player).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "category_${id}_quests_remaining") { player ->
            getRemainingCount(player).toNiceString()
        }.register()
    }

    private fun quests() = Quests.values().filter { it.category == this }

    fun isCompleted(player: OfflinePlayer): Boolean {
        val quests = quests()
        return quests.isNotEmpty() && quests.all { it.hasCompleted(player) }
    }

    fun getCompletedCount(player: OfflinePlayer): Int =
        quests().count { it.hasCompleted(player) }

    fun getRemainingCount(player: OfflinePlayer): Int =
        quests().count { !it.hasCompleted(player) }

    fun checkAndComplete(player: Player) {
        val quests = quests()
        if (quests.isNotEmpty() && quests.all { it.hasCompleted(player) }) {
            effects?.trigger(player.toDispatcher())
            Bukkit.getPluginManager().callEvent(PlayerCategoryCompleteEvent(player, this))
        }
    }

    override fun onRegister() {}
    override fun onRemove() {}
}
