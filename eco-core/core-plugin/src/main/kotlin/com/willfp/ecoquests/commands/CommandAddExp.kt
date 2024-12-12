package com.willfp.ecoquests.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.commands.notifyNull
import com.willfp.eco.util.StringUtils
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandAddExp(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "addexp",
    "ecoquests.command.addexp",
    false
) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
        val quest = notifyNull(Quests[args.getOrNull(1)], "invalid-quest")

        val taskTemplate = notifyNull(Tasks[args.getOrNull(2)], "invalid-task")
        val task = notifyNull(quest.getTask(taskTemplate), "invalid-task")
        if (args.size < 4) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-exp-value"))
            return
        }

        val unparsedValue = args[3].notifyNull("invalid-exp-value")
        val value: Double
        try {
            value = unparsedValue.toDouble()
        } catch (_: NumberFormatException) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-exp-value"))
            return
        }

        task.gainExperience(player, value)

        sender.sendMessage(
            plugin.langYml.getMessage("exp-added", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%xp%", value.toString())
                .replace("%quest%", quest.name)
                .replace("%task%", task.template.id)
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

        if (args.size == 3) {
            val quest = Quests[args[1]]
            if (quest != null) {
                StringUtil.copyPartialMatches(
                    args[2],
                    quest.tasks.map { it.template.id } ,
                    completions
                )
            }
        }

        return completions
    }

}