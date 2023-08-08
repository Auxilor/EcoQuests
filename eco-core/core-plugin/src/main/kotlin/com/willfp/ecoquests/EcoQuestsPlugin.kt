package com.willfp.ecoquests

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.ecoquests.commands.CommandEcoQuests
import com.willfp.ecoquests.commands.CommandQuests
import com.willfp.ecoquests.gui.PreviousQuestsGUI
import com.willfp.ecoquests.gui.QuestsGUI
import com.willfp.ecoquests.quests.QuestCompleteDisplay
import com.willfp.ecoquests.quests.QuestStartDisplay
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import org.bukkit.Bukkit
import org.bukkit.event.Listener


class EcoQuestsPlugin : LibreforgePlugin() {
    override fun handleEnable() {
        PlayerlessPlaceholder(this, "quests_amount") {
            Quests.values().size.toString()
        }

        PlayerPlaceholder(this, "quests_completed") {
            Quests.getCompletedQuests(it).size.toString()
        }

        PlayerPlaceholder(this, "quests_active") {
            Quests.getActiveQuests(it).size.toString()
        }
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
