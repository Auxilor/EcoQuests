package com.willfp.ecoquests.tasks

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.lineWrap
import com.willfp.eco.util.toNiceString
import com.willfp.ecoquests.api.event.PlayerCompleteTaskEvent
import com.willfp.ecoquests.quests.Quests
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counters
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Task(
    private val plugin: EcoPlugin,
    override val id: String,
    val config: Config
) : KRegistrable {
    private val xpKey = PersistentDataKey(
        plugin.createNamespacedKey("task_${id}_xp"),
        PersistentDataKeyType.DOUBLE,
        0.0
    )

    private val hasCompletedKey = PersistentDataKey(
        plugin.createNamespacedKey("task_${id}_has_completed"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    private val xpGainMethods = config.getSubsections("xp-gain-methods").mapNotNull {
        Counters.compile(it, ViolationContext(plugin, "task $id tasks"))
    }

    private val accumulator = object : Accumulator {
        override fun accept(player: Player, count: Double) {
            this@Task.giveExperience(player, count)
        }
    }

    override fun onRegister() {
        for (counter in xpGainMethods) {
            counter.bind(accumulator)
        }
    }

    override fun onRemove() {
        for (counter in xpGainMethods) {
            counter.unbind()
        }
    }

    fun getDescription(player: Player): String {
        return config.getString("description")
            .replace("%xp%", getExperience(player).toNiceString())
            .replace("%required-xp%", getExperienceRequired(player).toNiceString())
            .formatEco(player)
    }

    fun getCompletedDescription(player: Player): List<String> {
        return if (hasCompleted(player)) {
            plugin.configYml.getString("tasks.completed")
                .replace("%description%", getDescription(player))
                .lineWrap(plugin.configYml.getInt("tasks.line-wrap"), true)
        } else {
            plugin.configYml.getString("tasks.not-completed")
                .replace("%description%", getDescription(player))
                .lineWrap(plugin.configYml.getInt("tasks.line-wrap"), true)
        }
    }

    fun hasCompleted(player: Player): Boolean {
        return player.profile.read(hasCompletedKey)
    }

    fun getExperience(player: Player): Double {
        return player.profile.read(xpKey)
    }

    fun getExperienceRequired(player: Player): Double {
        return config.getDoubleFromExpression("required-xp", player)
    }

    fun giveExperience(player: Player, amount: Double) {
        val requiredXp = getExperienceRequired(player)
        val newXp = player.profile.read(xpKey) + amount

        player.profile.write(xpKey, newXp)

        if (newXp >= requiredXp) {
            player.profile.write(hasCompletedKey, true)

            Bukkit.getPluginManager().callEvent(PlayerCompleteTaskEvent(player, this))

            // Then check if any quests are now completed
            for (quest in Quests.values()) {
                quest.checkCompletion(player)
            }
        }
    }
}
