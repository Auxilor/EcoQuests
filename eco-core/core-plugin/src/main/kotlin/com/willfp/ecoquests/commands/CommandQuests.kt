package com.willfp.ecoquests.commands

import com.willfp.ecoquests.EcoQuestsPlugin
import com.willfp.ecoquests.categories.Categories
import com.willfp.ecoquests.gui.QuestsGUI
import wiki.ecohub.core.command.playerCommand

internal fun EcoQuestsPlugin.questsCommand() = playerCommand(name = "quests", permission = "ecoquests.command.quests") {
    aliases("q", "quest")

    cancelCommand()

    executes {
        val categoryId = args.firstOrNull()

        if (categoryId == null) {
            QuestsGUI.open(sender)
        } else {
            QuestsGUI.open(sender, Categories[categoryId] ?: fail("invalid-category"))
        }
    }

    completes {
        if (args.size == 1) Categories.values().map { it.id } else emptyList()
    }
}
