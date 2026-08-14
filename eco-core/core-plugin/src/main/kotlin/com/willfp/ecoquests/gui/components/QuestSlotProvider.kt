package com.willfp.ecoquests.gui.components

import com.willfp.ecoquests.quests.Quest
import org.bukkit.entity.Player

interface QuestSlotProvider {
    fun getQuestAt(player: Player, page: Int, row: Int, column: Int, columnSize: Int, pageSize: Int): Quest?
    fun getPages(player: Player, pageSize: Int): Int
}
