package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.api.event.QuestEvent
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.filter.Filter
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.ui.text.containsIgnoreCase

object FilterQuest : Filter<NoCompileData, Collection<String>>("quest") {
    override val description = "Matches when the quest involved in the triggering event has one of the given IDs."

    override val categories = setOf("meta")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Passes automatically when the triggering event is not a quest event."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? QuestEvent ?: return true

        return value.containsIgnoreCase(event.quest.id)
    }
}
