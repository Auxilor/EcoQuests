package com.willfp.ecoquests.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoquests.plugin
import org.bukkit.command.CommandSender

object CommandEcoQuests : PluginCommand(
    plugin,
    "ecoquests",
    "ecoquests.command.ecoquests",
    false
) {
    init {
        this.addSubcommand(CommandReload)
            .addSubcommand(CommandStart)
            .addSubcommand(CommandResetPlayer)
            .addSubcommand(CommandReset)
            .addSubcommand(CommandAddExp)
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }
}
