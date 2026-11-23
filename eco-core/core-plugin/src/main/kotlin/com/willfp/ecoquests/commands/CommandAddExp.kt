package com.willfp.ecoquests.commands

import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.game.entity.runOwned

private const val TASK_ARGS = 3

internal fun CommandBuilder<CommandSender>.addExpCommand() = subcommand(
    name = "addexp",
    permission = "ecoquests.command.addexp"
) {
    executes {
        val player = onlinePlayer(0)
        val quest = quest(1)
        val taskTemplate = Tasks[args.getOrNull(2)] ?: fail("invalid-task")
        val task = quest.getTask(taskTemplate) ?: fail("invalid-task")

        val value = args.getOrNull(TASK_ARGS)?.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 }
            ?: fail("invalid-exp-value")

        player.runOwned {
            task.gainExperience(player, value)

            replyWithoutPlaceholders(
                "exp-added",
                "xp" to value.toString(),
                "quest" to quest.name,
                "task" to task.template.id,
                "player" to player.name
            )
        }
    }

    completes {
        when (args.size) {
            1 -> playerNames()
            2 -> questIds()
            TASK_ARGS -> Quests[args[1]]?.run { tasks.map { it.template.id } }.orEmpty()
            else -> emptyList()
        }
    }
}
