package com.willfp.ecoquests.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionHasCompletedTask : Condition<NoCompileData>("has_completed_task") {
    override val arguments = arguments {
        require("quest", "You must specify the quest ID!")
        require("task", "You must specify the task ID!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        val quest = Quests[config.getString("quest")] ?: return false
        val template = Tasks[config.getString("task")] ?: return false
        val task = quest.getTask(template) ?: return false

        return task.hasCompleted(player)
    }
}
