package com.willfp.ecoquests

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.ecoquests.commands.CommandEcoQuests
import com.willfp.ecoquests.commands.CommandQuests
import com.willfp.ecoquests.gui.PreviousQuestsGUI
import com.willfp.ecoquests.gui.QuestsGUI
import com.willfp.ecoquests.libreforge.ConditionHasCompletedQuest
import com.willfp.ecoquests.libreforge.ConditionHasCompletedTask
import com.willfp.ecoquests.libreforge.ConditionHasQuestActive
import com.willfp.ecoquests.libreforge.EffectGainTaskXp
import com.willfp.ecoquests.libreforge.EffectGiveTaskXp
import com.willfp.ecoquests.libreforge.EffectStartQuest
import com.willfp.ecoquests.libreforge.FilterQuest
import com.willfp.ecoquests.libreforge.FilterTask
import com.willfp.ecoquests.libreforge.TriggerCompleteQuest
import com.willfp.ecoquests.libreforge.TriggerCompleteTask
import com.willfp.ecoquests.libreforge.TriggerGainTaskXp
import com.willfp.ecoquests.libreforge.TriggerStartQuest
import com.willfp.ecoquests.quests.QuestCompleteDisplay
import com.willfp.ecoquests.quests.QuestStartDisplay
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.event.Listener


class EcoQuestsPlugin : LibreforgePlugin() {
    override fun handleEnable() {
        Conditions.register(ConditionHasCompletedQuest)
        Conditions.register(ConditionHasCompletedTask)
        Conditions.register(ConditionHasQuestActive)
        Effects.register(EffectGainTaskXp)
        Effects.register(EffectGiveTaskXp)
        Effects.register(EffectStartQuest)
        Filters.register(FilterQuest)
        Filters.register(FilterTask)
        Triggers.register(TriggerCompleteQuest)
        Triggers.register(TriggerCompleteTask)
        Triggers.register(TriggerGainTaskXp)
        Triggers.register(TriggerStartQuest)

        PlayerlessPlaceholder(this, "quests_amount") {
            Quests.values().size.toString()
        }.register()

        PlayerPlaceholder(this, "quests_completed") {
            Quests.getCompletedQuests(it).size.toString()
        }.register()

        PlayerPlaceholder(this, "quests_active") {
            Quests.getActiveQuests(it).size.toString()
        }.register()
    }

    override fun handleReload() {
        PreviousQuestsGUI.reload(this)
        QuestsGUI.reload(this)
    }

    override fun createTasks() {
        val scanInterval = this.configYml.getInt("scan-interval").toLong()
        this.scheduler.runTimer(scanInterval, scanInterval) {
            for (quest in Quests.values()) {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (quest.shouldStart(player)) {
                        quest.start(player)
                    }
                }
            }
        }

        this.scheduler.runTimer(20, 20) {
            for (quest in Quests.values()) {
                quest.resetIfNeeded()
            }
        }
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            QuestCompleteDisplay(this),
            QuestStartDisplay(this)
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoQuests(this),
            CommandQuests(this)
        )
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            Tasks,
            Quests
        )
    }
}
