package com.willfp.ecoquests.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionHasCompletedTask : Condition<NoCompileData>("has_completed_task") {
    override val description = "Passes when the player has completed the specified task within the specified quest."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "quest",
            "You must specify the quest ID!",
            description = "The ID of the quest that contains the task.",
            type = ArgType.STRING
        )
        require(
            "task",
            "You must specify the task ID!",
            description = "The ID of the task that must be completed.",
            type = ArgType.STRING
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
