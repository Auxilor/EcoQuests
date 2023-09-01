package com.willfp.ecoquests.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import org.bukkit.entity.Player

class PositionedPageChanger(
    config: Config,
    direction: PageChanger.Direction
): PositionedComponent {
    private val pageChanger = PageChanger(
        Items.lookup(config.getString("item")).item,
        direction
    )

    override val row = config.getInt("location.row")

    override val column = config.getInt("location.column")

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot? {
        return pageChanger.getSlotAt(row, column, player, menu)
    }
}
