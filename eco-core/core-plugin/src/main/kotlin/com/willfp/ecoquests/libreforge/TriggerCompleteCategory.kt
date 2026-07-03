package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.api.event.PlayerCategoryCompleteEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCompleteCategory : Trigger("complete_category") {
    override val description = "Fires when the player completes a category."

    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: PlayerCategoryCompleteEvent) {
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
