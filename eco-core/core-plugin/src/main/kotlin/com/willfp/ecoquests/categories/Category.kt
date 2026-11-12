package com.willfp.ecoquests.categories

import com.willfp.ecoquests.api.event.PlayerCategoryCompleteEvent
import com.willfp.ecoquests.gui.QuestsGUI
import com.willfp.ecoquests.quests.Quests
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.KRegistrable
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.game.item.Items
import wiki.ecohub.game.item.modify
import wiki.ecohub.modules.menus.SlotLocation
import wiki.ecohub.modules.menus.onLeftClick
import wiki.ecohub.modules.menus.slot
import wiki.ecohub.rules.chain.NormalExecutorFactory
import wiki.ecohub.rules.effect.Effects
import wiki.ecohub.ui.placeholder.type.PlayerPlaceholder
import wiki.ecohub.ui.placeholder.type.PlayerlessPlaceholder
import wiki.ecohub.ui.text.formatEco

class Category(
    private val plugin: EcoPlugin,
    override val id: String,
    val config: Config
) : KRegistrable {
    val name = config.getFormattedString("name")

    val showsInGui = config.getBoolOrNull("gui.enabled") != false

    val position: SlotLocation? = config.getSubsectionOrNull("gui.position")?.let {
        SlotLocation(row = it.getInt("row"), column = it.getInt("column"))
    }

    private val guiItem = Items.lookup(config.getStringOrNull("gui.item") ?: "book").getItem()

    val slot = slot({ player, _ ->
        guiItem.clone().modify {
            val lines = addPlaceholdersInto(
                listOf(plugin.configYml.getString("categories-gui.category-icon.name")) +
                    config.getStrings("gui.lore") +
                    plugin.configYml.getStrings("categories-gui.category-icon.lore"),
                player
            )

            setDisplayName(lines.first())
            addLoreLines(lines.drop(1))
        }
    }) {
        onLeftClick { player, _, _, _ ->
            QuestsGUI.open(player, this@Category)
        }
    }

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

    private fun addPlaceholdersInto(lines: List<String>, player: Player): List<String> {
        val quests = quests()
        val completed = quests.count { it.hasCompleted(player) }.toNiceString()
        val active = quests.count { it.hasActive(player) }.toNiceString()

        return lines.map { line ->
            line.replace(oldValue = "%category%", newValue = name)
                .replace(oldValue = "%quests_completed%", newValue = completed)
                .replace(oldValue = "%quests_active%", newValue = active)
                .replace(oldValue = "%quests_total%", newValue = quests.size.toNiceString())
                .formatEco(player, formatPlaceholders = true)
        }
    }

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
