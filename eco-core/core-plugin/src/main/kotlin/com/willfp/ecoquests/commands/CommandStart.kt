package com.willfp.ecoquests.commands

import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.game.entity.runOwned

internal fun CommandBuilder<CommandSender>.startCommand() = subcommand(
    name = "start",
    permission = "ecoquests.command.start"
) {
    executes {
        val player = onlinePlayer(0)
        val quest = quest(1)

        if (quest.hasCompleted(player)) {
            fail("cannot-start-completed")
        }

        if (quest.hasStarted(player)) {
            fail("already-started")
        }

        player.runOwned {
            quest.start(player)
            replyWithoutPlaceholders("started-quest", "quest" to quest.name, "player" to player.name)
        }
    }

    completes {
        when (args.size) {
            1 -> playerNames()
            2 -> questIds()
            else -> emptyList()
        }
    }
}
