package com.willfp.ecoquests.gui.components

import com.willfp.eco.util.MenuUtils
import com.willfp.ecoquests.quests.Quest
import org.bukkit.entity.Player

class FlatListQuestSlotProvider(
    private val getQuests: (Player) -> List<Quest>
) : QuestSlotProvider {
    override fun getQuestAt(
        player: Player,
        page: Int,
        row: Int,
        column: Int,
        columnSize: Int,
        pageSize: Int
    ): Quest? {
        val index = MenuUtils.rowColumnToSlot(row, column, columnSize) + ((page - 1) * pageSize)

        return getQuests(player)
            .filter { it.showsInGui }
            .getOrNull(index)
    }

    override fun getPages(player: Player, pageSize: Int): Int {
        return getQuests(player).size.floorDiv(pageSize) + 1
    }
}
