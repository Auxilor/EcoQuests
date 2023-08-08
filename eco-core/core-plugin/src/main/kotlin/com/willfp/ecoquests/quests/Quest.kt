package com.willfp.ecoquests.quests

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.formatEco
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Quest(
    private val plugin: EcoPlugin,
    override val id: String,
    val config: Config
) : KRegistrable {
    val name = config.getFormattedString("name")

    private val guiItem = Items.lookup(config.getString("gui.item")).item

    val slot = slot({ player, _ ->
        guiItem.clone().modify {
            addLoreLines(
                plugin.configYml.getStrings("gui.quest-area.quest-icon.lore")
                    .flatMap {
                        if (it == "%tasks%") tasks.map { task -> task.getCompletedDescription(player) }
                        else listOf(it)
                    }.formatEco(player)
            )
        }
    }) {

    }

    val tasks = config.getStrings("tasks").mapNotNull { Tasks[it] }

    val hasStartedKey: PersistentDataKey<Boolean> = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_has_started"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    val hasCompletedKey: PersistentDataKey<Boolean> = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_has_completed"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    val rewards = Effects.compileChain(
        config.getSubsections("rewards"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "quest $id rewards")
    )

    val startEffects = Effects.compileChain(
        config.getSubsections("start-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "quest $id start-effects")
    )

    val startConditions = Conditions.compile(
        config.getSubsections("start-conditions"),
        ViolationContext(plugin, "quest $id start-conditions")
    )

    fun shouldStart(player: Player): Boolean {
        return startConditions.areMet(player, EmptyProvidedHolder)
                && !hasStarted(player)
    }

    fun hasStarted(player: Player): Boolean {
        return player.profile.read(hasStartedKey)
    }

    fun start(player: Player) {
        if (hasStarted(player)) {
            return
        }

        startEffects?.trigger(player)
        player.profile.write(hasStartedKey, true)
    }

    fun checkCompletion(player: Player): Boolean {
        // Check if the player has completed the Quest before
        if (player.profile.read(hasCompletedKey)) {
            return true
        }

        if (tasks.all { it.hasCompleted(player) }) {
            player.profile.write(hasCompletedKey, true)
            rewards?.trigger(player)

            return true
        }

        return false
    }
}
