package com.willfp.ecoquests.commands

import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player
import wiki.ecohub.core.command.CommandBuilder

internal fun CommandBuilder<Player>.cancelCommand() = subcommand(
    name = "cancel",
    permission = "ecoquests.command.quests.cancel"
) {
    executes {
        val quest = quest(0)

        if (!quest.hasActive(sender)) {
            fail("quest-not-active")
        }

        quest.reset(sender)

        replyWithoutPlaceholders("cancelled-quest", "quest" to quest.name)
    }

    completes {
        if (args.size == 1) Quests.getActiveQuests(sender).map { it.id } else emptyList()
    }
}
