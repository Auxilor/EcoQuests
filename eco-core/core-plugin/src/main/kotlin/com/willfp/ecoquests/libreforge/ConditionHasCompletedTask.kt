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
        require("task", "You must specify the task ID!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        val task = Tasks[config.getString("task")] ?: return false

        return task.hasCompleted(player)
    }
}
