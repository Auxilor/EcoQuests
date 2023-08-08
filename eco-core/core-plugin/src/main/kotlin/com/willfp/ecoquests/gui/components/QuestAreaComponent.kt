package com.willfp.ecoquests.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.util.MenuUtils
import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player

class QuestAreaComponent(
    config: Config,
    private val getQuests: (Player) -> List<Quest>
): PositionedComponent {
    override val row = config.getInt("top-left.row")
    override val column = config.getInt("top-left.column")

    override val rowSize = config.getInt("bottom-right.row") - row + 1
    override val columnSize = config.getInt("bottom-right.column") - column + 1

    private val pageSize = rowSize * columnSize

    fun getPages(player: Player): Int {
        return getQuests(player).size.floorDiv(pageSize) + 1
    }

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot? {
        val page = menu.getPage(player)

        val index = MenuUtils.rowColumnToSlot(row, column, columnSize) + ((page - 1) * pageSize)

        return getQuests(player)
            .getOrNull(index)
            ?.slot
    }
}
