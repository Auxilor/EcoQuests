package com.willfp.ecoquests.gui.components

import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.modules.menus.SlotLocation
import wiki.ecohub.modules.menus.component.PositionedComponent
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.modules.menus.slot.Slot

class QuestAreaComponent(
    config: Config,
    private val provider: QuestSlotProvider
) : PositionedComponent {
    private val row = config.getInt("top-left.row")
    private val column = config.getInt("top-left.column")

    override val locations = listOf(SlotLocation(row, column))

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
