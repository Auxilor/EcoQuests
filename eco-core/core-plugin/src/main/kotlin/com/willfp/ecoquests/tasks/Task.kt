package com.willfp.ecoquests.tasks

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.lineWrap
import com.willfp.eco.util.toNiceString
import com.willfp.ecoquests.api.event.PlayerTaskCompleteEvent
import com.willfp.ecoquests.api.event.PlayerTaskExpGainEvent
import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.quests.Quests
import com.willfp.libreforge.counters.Accumulator
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import kotlin.math.min

class Task(
    private val plugin: EcoPlugin,
    val template: TaskTemplate,
    val quest: Quest,
    internal val xpExpr: String
) {
    private val xpKey = PersistentDataKey(
        plugin.createNamespacedKey("${quest.id}_task_${template.id}_xp"),
        PersistentDataKeyType.DOUBLE,
        0.0
    )

    private val hasCompletedKey = PersistentDataKey(
        plugin.createNamespacedKey("${quest.id}_task_${template.id}_has_completed"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    private val xpGainMethods = template.xpGainMethods.map { it.clone() }

    private val accumulator = object : Accumulator {
        override fun accept(player: Player, count: Double) {
            if (!quest.hasActive(player)) {
                return
            }

            this@Task.gainExperience(player, count)
        }
    }

    init {
        PlayerPlaceholder(plugin, "${quest.id}_task_${template.id}_required_xp") {
            getExperienceRequired(it).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${quest.id}_task_${template.id}_xp") {
            getExperience(it).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${quest.id}_task_${template.id}_description") {
            getDescription(it)
        }.register()

        PlayerPlaceholder(plugin, "${quest.id}_task_${template.id}_completed") {
            hasCompleted(it).toNiceString()
        }.register()
    }

    fun bind() {
        for (counter in xpGainMethods) {
            counter.bind(accumulator)
        }
    }

    fun unbind() {
        for (counter in xpGainMethods) {
            counter.unbind()
        }
    }

    fun reset(player: OfflinePlayer) {
        player.profile.write(xpKey, 0.0)
        player.profile.write(hasCompletedKey, false)
    }

    fun hasCompleted(player: OfflinePlayer): Boolean {
        return player.profile.read(hasCompletedKey)
    }

    fun getExperienceRequired(player: Player): Double {
        return evaluateExpression(
            xpExpr,
            placeholderContext(
                player = player
            )
        )
    }

    fun getExperience(player: OfflinePlayer): Double {
        return player.profile.read(xpKey)
    }

    /**
     * Gain experience naturally.
     */
    fun gainExperience(player: Player, amount: Double) {
        val event = PlayerTaskExpGainEvent(player, template, quest, amount)

        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return
        }

        giveExperience(player, event.amount)
    }

    /**
     * Give experience directly
     */
    fun giveExperience(player: Player, amount: Double) {
        val requiredXp = getExperienceRequired(player)
        val newXp = player.profile.read(xpKey) + amount

        player.profile.write(xpKey, min(newXp, requiredXp))

        if (newXp >= requiredXp) {
            player.profile.write(hasCompletedKey, true)

            Bukkit.getPluginManager().callEvent(PlayerTaskCompleteEvent(player, template, quest))

            // Then check if any quests are now completed
            for (quest in Quests.values()) {
                quest.checkCompletion(player)
            }
        }
    }

    fun getDescription(player: Player): String {
        return template.config.getString("description")
            .replace("%xp%", getExperience(player).toNiceString())
            .replace("%required-xp%", getExperienceRequired(player).toNiceString())
            .formatEco(player)
    }

    fun getCompletedDescription(player: Player): String {
        return if (hasCompleted(player)) {
            plugin.configYml.getString("tasks.completed")
                .replace("%description%", getDescription(player))
        } else {
            plugin.configYml.getString("tasks.not-completed")
                .replace("%description%", getDescription(player))
        }
    }
}
