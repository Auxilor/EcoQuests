package com.willfp.ecoquests.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoquests.categories.Categories
import com.willfp.ecoquests.gui.QuestsGUI
import com.willfp.ecoquests.plugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

object CommandQuests : PluginCommand(
    plugin,
    "quests",
    "ecoquests.command.quests",
    true
) {
    init {
        this.addSubcommand(CommandCancel)
    }

    override fun getAliases(): List<String> {
        return listOf(
            "q",
            "quest"
        )
    }

    override fun onExecute(sender: Player, args: List<String>) {
        if (args.isEmpty()) {
            QuestsGUI.open(sender)
            return
        }

        QuestsGUI.open(sender, notifyNull(Categories[args[0]], "invalid-category"))
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Categories.values().map { it.id },
                completions
            )
        }

        return completions
    }
}
