package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.api.event.PlayerTaskExpGainEvent
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.Trigger
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object TriggerGainTaskXp : Trigger("gain_task_xp") {
    override val description = "Fires when the player gains experience towards a task."

    override val categories = setOf("economy")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VALUE to "The amount of task experience gained."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    @EventHandler
    fun handle(event: PlayerTaskExpGainEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                value = event.amount
            )
        )
    }
}
