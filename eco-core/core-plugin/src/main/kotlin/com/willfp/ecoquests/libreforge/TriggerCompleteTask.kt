package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.api.event.PlayerTaskCompleteEvent
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.Trigger
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object TriggerCompleteTask : Trigger("complete_task") {
    override val description = "Fires when the player completes a task within a quest."

    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: PlayerTaskCompleteEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event
            )
        )
    }
}
