package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.quests.Quest
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerQuestCompleteEvent(
    who: Player,
    override val quest: Quest
): PlayerEvent(who), QuestEvent {
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
