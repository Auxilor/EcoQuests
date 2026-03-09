package com.willfp.ecoquests.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quests
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandReset : PluginCommand(
    plugin,
    "reset",
    "ecoquests.command.reset",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val quest = notifyNull(Quests[args.getOrNull(0)], "invalid-quest")

        if (!quest.isResettable) {
            sender.sendMessage(
                plugin.langYml.getMessage("quest-not-resettable", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%quest%", quest.name)
            )
            return
        }

        quest.reset()

        sender.sendMessage(
            plugin.langYml.getMessage("reset-quest", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%quest%", quest.name)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Quests.values().map { it.id },
                completions
            )
        }

        return completions
    }
}

