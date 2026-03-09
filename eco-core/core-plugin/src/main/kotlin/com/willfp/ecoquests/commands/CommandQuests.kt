package com.willfp.ecoquests.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoquests.gui.QuestsGUI
import com.willfp.ecoquests.plugin
import org.bukkit.entity.Player

object CommandQuests : PluginCommand(
    plugin,
    "quests",
    "ecoquests.command.quests",
    true
) {
    override fun getAliases(): List<String> {
        return listOf(
            "q",
            "quest"
        )
    }

    override fun onExecute(sender: Player, args: List<String>) {
        QuestsGUI.open(sender)
    }
}
