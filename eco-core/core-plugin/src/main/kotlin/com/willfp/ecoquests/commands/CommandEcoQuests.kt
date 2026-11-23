package com.willfp.ecoquests.commands

import com.willfp.ecoquests.EcoQuestsPlugin
import wiki.ecohub.core.command.command

internal fun EcoQuestsPlugin.ecoQuestsCommand() = command(
    name = "ecoquests",
    permission = "ecoquests.command.ecoquests"
) {
    reloadCommand()
    startCommand()
    resetPlayerCommand()
    resetCommand()
    addExpCommand()
}
