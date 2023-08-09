package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.tasks.TaskTemplate
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerTaskCompleteEvent(
    who: Player,
    override val task: TaskTemplate,
    override val quest: Quest
): PlayerEvent(who), TaskEvent, QuestEvent {
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
