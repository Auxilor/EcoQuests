package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.quests.Quest
import org.bukkit.entity.Player

interface QuestEvent {
    val quest: Quest

    val player: Player
}
