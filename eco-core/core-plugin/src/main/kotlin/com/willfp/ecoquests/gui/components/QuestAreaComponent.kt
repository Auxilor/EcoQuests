package com.willfp.ecoquests.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.entity.Player

class QuestAreaComponent(
    config: Config,
    private val provider: QuestSlotProvider
) : PositionedComponent {
    override val row = config.getInt("top-left.row")
    override val column = config.getInt("top-left.column")

    override val rowSize = config.getInt("bottom-right.row") - row + 1
    override val columnSize = config.getInt("bottom-right.column") - column + 1

    private val pageSize = rowSize * columnSize

    fun getPages(player: Player): Int {
        return provider.getPages(player, pageSize)
    }

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot? {
        val page = menu.getPage(player)

        return provider.getQuestAt(player, page, row, column, columnSize, pageSize)?.slot
    }
}
