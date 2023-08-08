package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.tasks.Task
import org.bukkit.entity.Player

interface TaskEvent {
    val task: Task

    val player: Player
}
