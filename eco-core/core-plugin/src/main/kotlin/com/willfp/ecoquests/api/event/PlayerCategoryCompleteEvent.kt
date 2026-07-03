package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.categories.Category
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerCategoryCompleteEvent(
    who: Player,
    val category: Category
) : PlayerEvent(who), Cancellable {
    private var cancelled = false

    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }

    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}
