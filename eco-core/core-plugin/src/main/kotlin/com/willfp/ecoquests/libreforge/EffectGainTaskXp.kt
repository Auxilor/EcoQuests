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

object EffectGainTaskXp : Effect<NoCompileData>("gain_task_xp") {
    override val description = "Gives the player experience towards a task as if they had earned it naturally, " +
        "firing the normal task xp gain event so other effects can react to it."

    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "task",
            "You must specify the task ID!",
            description = "The ID of the task to give experience towards.",
            type = ArgType.STRING
        )
        require(
            "quest",
            "You must specify the quest ID!",
            description = "The ID of the quest that contains the task.",
            type = ArgType.STRING
        )
        require(
            "xp",
            "You must specify the amount of xp to gain!",
            description = "The amount of task experience to gain. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val quest = Quests[config.getString("quest")] ?: return false
        val template = Tasks[config.getString("task")] ?: return false
        val task = quest.getTask(template) ?: return false

        val xp = config.getDoubleFromExpression("xp", data)

        task.gainExperience(player, xp)

        return true
    }
}
