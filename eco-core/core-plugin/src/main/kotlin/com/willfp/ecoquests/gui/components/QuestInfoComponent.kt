package com.willfp.ecoquests.gui.components

import com.willfp.ecoquests.gui.PreviousQuestsGUI
import wiki.ecohub.config.Config
import wiki.ecohub.game.item.Items
import wiki.ecohub.game.item.modify
import wiki.ecohub.modules.menus.component.PositionedComponent
import wiki.ecohub.modules.menus.getSlotLocations
import wiki.ecohub.modules.menus.onLeftClick
import wiki.ecohub.modules.menus.slot
import wiki.ecohub.ui.text.formatEco

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

    override val isEnabled = config.getBoolOrNull("enabled") ?: true
    override val locations = config.getSlotLocations()

    override fun getSlotAt(row: Int, column: Int) = slot
}
