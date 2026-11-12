package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object EffectStartQuest : Effect<NoCompileData>("start_quest") {
    override val description = "Starts the specified quest for the player, as if they had accepted it."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Has no effect if the player has already started the quest."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "quest",
            "You must specify the quest!",
            description = "The ID of the quest to start.",
            type = ArgType.STRING,
            example = "mining_master"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val quest = Quests[config.getString("quest")] ?: return false

        quest.start(player)

        return true
    }
}
