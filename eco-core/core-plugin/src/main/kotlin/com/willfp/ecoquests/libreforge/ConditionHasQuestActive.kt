package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get

object ConditionHasQuestActive : Condition<NoCompileData>("has_quest_active") {
    override val description = "Passes when the player has started the specified quest but not yet completed it."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "quest",
            "You must specify the quest ID!",
            description = "The ID of the quest to check.",
            type = ArgType.STRING,
            example = "mining_master"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val quest = Quests[config.getString("quest")] ?: return false

        return quest.hasActive(player)
    }
}
