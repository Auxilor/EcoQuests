package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getDoubleFromExpression
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

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
            type = ArgType.STRING,
            example = "chop_10_logs"
        )
        require(
            "quest",
            "You must specify the quest ID!",
            description = "The ID of the quest that contains the task.",
            type = ArgType.STRING,
            example = "mining_master"
        )
        require(
            "xp",
            "You must specify the amount of xp to gain!",
            description = "The amount of task experience to gain.",
            type = ArgType.EXPRESSION,
            example = "10 + %level% * 5"
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
