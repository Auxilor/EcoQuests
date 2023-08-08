package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.tasks.Task
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerTaskExpGainEvent(
    who: Player,
    override val task: Task,
    var amount: Double
) : PlayerEvent(who), TaskEvent, Cancellable {
    private var cancelled = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override val player: Player = super.player

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}
