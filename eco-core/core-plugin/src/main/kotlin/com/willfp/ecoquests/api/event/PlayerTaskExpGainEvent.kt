package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.tasks.TaskTemplate
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerTaskExpGainEvent(
    who: Player,
    override val task: TaskTemplate,
    override val quest: Quest,
    var amount: Double
) : PlayerEvent(who), TaskEvent, QuestEvent, Cancellable {
    private var cancelled = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

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
