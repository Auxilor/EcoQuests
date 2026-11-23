package com.willfp.ecoquests.commands

import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.quests.Quests
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import wiki.ecohub.core.command.CommandContext
import wiki.ecohub.ui.text.StringUtils

internal fun CommandContext<*>.onlinePlayer(index: Int): Player =
    argument(index = index, missingKey = "invalid-player", invalidKey = "invalid-player") { Bukkit.getPlayer(it) }

internal fun CommandContext<*>.quest(index: Int): Quest = Quests[args.getOrNull(index)] ?: fail("invalid-quest")

internal fun questIds(): List<String> = Quests.values().map { it.id }

/**
 * Send the lang message [key] with [replacements], without translating placeholders in it first.
 */
internal fun CommandContext<*>.replyWithoutPlaceholders(key: String, vararg replacements: Pair<String, String>) {
    val message = plugin.langYml.getMessage(key, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)

    sender.sendMessage(
        replacements.fold(message) { replaced, (placeholder, value) ->
            replaced.replace(oldValue = "%$placeholder%", newValue = value)
        }
    )
}
