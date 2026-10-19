package com.willfp.ecoquests.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.component.PositionedComponent
import com.willfp.eco.core.gui.getSlotLocations
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.ecoquests.gui.QuestsGUI

class BackButton(
    config: Config
) : PositionedComponent {
    private val slot = slot(
        Items.lookup(config.getString("item"))
    ) {
        onLeftClick { player, _, _, _ ->
            QuestsGUI.open(player)
        }
    }

    override val locations = config.getSlotLocations()

    override fun getSlotAt(row: Int, column: Int) = slot
}
