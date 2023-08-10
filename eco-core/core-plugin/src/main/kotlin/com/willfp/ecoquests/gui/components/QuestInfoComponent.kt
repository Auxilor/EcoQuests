package com.willfp.ecoquests.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.util.formatEco
import com.willfp.ecoquests.gui.PreviousQuestsGUI

class QuestInfoComponent(
    config: Config
) : PositionedComponent {
    private val baseItem = Items.lookup(config.getString("item"))

    private val slot = slot({ player, _ ->
        baseItem.item.clone().modify {
            setDisplayName(config.getString("name").formatEco(player, formatPlaceholders = true))
            addLoreLines(config.getStrings("lore").formatEco(player, formatPlaceholders = true))
        }
    }) {
        onLeftClick { player, _, _, _ ->
            PreviousQuestsGUI.open(player)
        }
    }

    override val row: Int = config.getInt("location.row")
    override val column: Int = config.getInt("location.column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
