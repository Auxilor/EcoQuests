package com.willfp.ecoquests.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

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
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val quest = Quests[config.getString("quest")] ?: return false

        quest.start(player)

        return true
    }
}
