package com.willfp.ecoquests.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quests
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

object CommandCancel : PluginCommand(
    plugin,
    "cancel",
    "ecoquests.command.quests.cancel",
    true
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val quest = notifyNull(Quests[args.getOrNull(0)], "invalid-quest")

        if (!quest.hasActive(player)) {
            sender.sendMessage(plugin.langYml.getMessage("quest-not-active"))
            return
        }

        quest.reset(player)

        sender.sendMessage(
            plugin.langYml.getMessage("cancelled-quest", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%quest%", quest.name)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1 && sender is Player) {
            StringUtil.copyPartialMatches(
                args[0],
                Quests.getActiveQuests(sender).map { it.id },
                completions
            )
        }

        return completions
    }
}
