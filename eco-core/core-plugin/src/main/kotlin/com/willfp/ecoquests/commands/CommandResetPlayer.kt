package com.willfp.ecoquests.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoquests.quests.Quests
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandResetPlayer(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "resetplayer",
    "ecoquests.command.resetplayer",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
        val quest = notifyNull(Quests[args.getOrNull(1)], "invalid-quest")

        quest.reset(player)

        sender.sendMessage(
            plugin.langYml.getMessage("reset-quest-for-player", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%quest%", quest.name)
                .replace("%player%", player.name)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                plugin.server.onlinePlayers.map { it.name },
                completions
            )
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                Quests.values().map { it.id },
                completions
            )
        }

        return completions
    }
}

