package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get

object ConditionHasCompletedTask : Condition<NoCompileData>("has_completed_task") {
    override val description = "Passes when the player has completed the specified task within the specified quest."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "quest",
            "You must specify the quest ID!",
            description = "The ID of the quest that contains the task.",
            type = ArgType.STRING,
            example = "mining_master"
        )
        require(
            "task",
            "You must specify the task ID!",
            description = "The ID of the task that must be completed.",
            type = ArgType.STRING,
            example = "chop_10_logs"
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
        val template = Tasks[config.getString("task")] ?: return false
        val task = quest.getTask(template) ?: return false

        return task.hasCompleted(player)
    }
}
