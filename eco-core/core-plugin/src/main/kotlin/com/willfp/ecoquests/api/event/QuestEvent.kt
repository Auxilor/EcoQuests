package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.quests.Quest

interface QuestEvent {
    val quest: Quest
}
