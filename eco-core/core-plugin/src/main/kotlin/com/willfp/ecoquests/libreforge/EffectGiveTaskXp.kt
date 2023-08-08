package com.willfp.ecoquests.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object EffectGiveTaskXp : Effect<NoCompileData>("give_task_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("task", "You must specify the task ID!")
        require("xp", "You must specify the amount of xp to give!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val task = Tasks[config.getString("task")] ?: return false
        val xp = config.getDoubleFromExpression("xp", data)

        task.giveExperience(player, xp)

        return true
    }
}
