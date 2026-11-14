package com.willfp.ecoquests.gui.components

import com.willfp.ecoquests.categories.Category
import com.willfp.ecoquests.plugin
import org.bukkit.entity.Player
import wiki.ecohub.modules.menus.SlotLocation
import wiki.ecohub.modules.menus.component.PositionedComponent
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.modules.menus.slot.Slot
import wiki.ecohub.modules.menus.slot.SlotArea

/**
 * Places category icons in an area: categories with a gui.position go to that
 * slot, the rest fill the free slots in order.
 */
class CategoryAreaComponent(
    area: SlotArea,
    categories: List<Category>
) : PositionedComponent {
    override val locations = listOf(SlotLocation(row = area.row, column = area.column))

    override val rowSize = area.rows

    override val columnSize = area.columns

    private val categoriesBySlot: Map<SlotLocation, Category> = buildMap {
        val unplaced = ArrayDeque<Category>()

        for (category in categories) {
            val local = category.position?.let {
                SlotLocation(row = it.row - area.row + 1, column = it.column - area.column + 1)
            }

            if (local != null && local.isInside(area) && local !in this) {
                put(local, category)
            } else {
                if (local != null) {
                    plugin.logger.warning(
                        "Category '${category.id}' has gui.position outside categories-gui.category-area " +
                            "or on another category; falling back to automatic placement."
                    )
                }
                unplaced.add(category)
            }
        }

        val freeSlots = (1..area.rows)
            .flatMap { row -> (1..area.columns).map { column -> SlotLocation(row = row, column = column) } }
            .filterNot { it in this }

        for ((slotLocation, category) in freeSlots.zip(unplaced)) {
            put(slotLocation, category)
        }

        if (unplaced.size > freeSlots.size) {
            plugin.logger.warning(
                "categories-gui.category-area is too small; " +
                    "${unplaced.size - freeSlots.size} categories are not shown."
            )
        }
    }

    private fun SlotLocation.isInside(area: SlotArea): Boolean =
        row in 1..area.rows && column in 1..area.columns

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot? =
        categoriesBySlot[SlotLocation(row = row, column = column)]?.slot
}
