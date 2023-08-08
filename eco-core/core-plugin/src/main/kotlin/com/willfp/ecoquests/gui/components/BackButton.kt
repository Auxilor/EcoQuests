package com.willfp.ecoquests.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.ecoquests.gui.QuestsGUI

class BackButton(
    config: Config
) : PositionedComponent {
    private val slot = slot(
        Items.lookup(config.getString("item"))
    ) {
        onLeftClick { player, _, _, _ ->
            player.closeInventory()
        }
    }

    override val row: Int = config.getInt("location.row")
    override val column: Int = config.getInt("location.column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
