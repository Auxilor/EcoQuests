package com.willfp.ecoquests.commands

import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder

internal fun CommandBuilder<CommandSender>.resetPlayerCommand() = subcommand(
    name = "resetplayer",
    permission = "ecoquests.command.resetplayer"
) {
    executes {
        val player = onlinePlayer(0)
        val quest = quest(1)

        quest.reset(player)

        replyWithoutPlaceholders("reset-quest-for-player", "quest" to quest.name, "player" to player.name)
    }

    completes {
        when (args.size) {
            1 -> playerNames()
            2 -> questIds()
            else -> emptyList()
        }
    }
}
