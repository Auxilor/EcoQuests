package com.willfp.ecoquests.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.core.platform.runGlobal

/**
 * Resetting a quest for everyone runs on the global region.
 */
internal fun CommandBuilder<CommandSender>.resetCommand() = subcommand(
    name = "reset",
    permission = "ecoquests.command.reset"
) {
    executes {
        val quest = quest(0)

        if (quest.isResettable) {
            Bukkit.getServer().runGlobal {
                quest.reset()
                replyWithoutPlaceholders("reset-quest", "quest" to quest.name)
            }
        } else {
            replyWithoutPlaceholders("quest-not-resettable", "quest" to quest.name)
        }
    }

    completes {
        if (args.size == 1) questIds() else emptyList()
    }
}
