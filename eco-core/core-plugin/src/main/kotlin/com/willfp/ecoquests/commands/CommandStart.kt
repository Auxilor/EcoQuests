package com.willfp.ecoquests.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoquests.gui.QuestsGUI
import com.willfp.ecoquests.quests.Quests
import com.willfp.libreforge.commands.CommandReload
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandStart(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "start",
    "ecoquests.command.start",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
        val quest = notifyNull(Quests[args.getOrNull(1)], "invalid-quest")

        if (quest.hasStarted(player) || quest.hasCompleted(player)) {
            sender.sendMessage(plugin.langYml.getMessage("already-started"))
            return
        }

        quest.start(player)
    }
}

