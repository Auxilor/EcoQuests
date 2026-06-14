package com.willfp.ecoquests.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecoquests.api.event.QuestEvent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

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
