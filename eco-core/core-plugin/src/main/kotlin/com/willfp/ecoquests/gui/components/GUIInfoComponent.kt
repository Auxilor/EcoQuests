package com.willfp.ecoquests.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder

class GUIInfoComponent(
    config: Config
) : PositionedComponent {
    private val slot = slot(
        ItemStackBuilder(Items.lookup(config.getString("item")))
            .setDisplayName(config.getFormattedString("name"))
            .addLoreLines(config.getFormattedStrings("lore"))
            .build()
    ) {
        onLeftClick { event, _ -> event.whoClicked.closeInventory() }
    }

    override val row: Int = config.getInt("location.row")
    override val column: Int = config.getInt("location.column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
