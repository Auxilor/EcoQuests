package com.willfp.ecoquests.gui

import com.willfp.eco.core.gui.addCustomSlots
import com.willfp.eco.core.gui.component.addComponent
import com.willfp.eco.core.gui.component.closeButton
import com.willfp.eco.core.gui.getSlotArea
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.setMask
import com.willfp.ecoquests.categories.Categories
import com.willfp.ecoquests.gui.components.CategoryAreaComponent
import com.willfp.ecoquests.gui.components.QuestInfoComponent
import com.willfp.ecoquests.plugin
import org.bukkit.entity.Player

object CategoriesGUI {
    @Volatile
    private var menu: Menu? = null

    fun reload() {
        if (!plugin.configYml.getBool("categories-gui.enabled")) {
            menu = null
            return
        }

        menu = menu(plugin.configYml.getInt("categories-gui.rows")) {
            title = plugin.configYml.getFormattedString("categories-gui.title")

            setMask(plugin.configYml, "categories-gui")

            addComponent(QuestInfoComponent(plugin.configYml.getSubsection("categories-gui.quest-info")))

            addComponent(closeButton(plugin.configYml.getSubsection("categories-gui.close")))

            addComponent(
                CategoryAreaComponent(
                    plugin.configYml.getSlotArea("categories-gui.category-area"),
                    Categories.values().filter { it.showsInGui }.sortedBy { it.id }
                )
            )

            addCustomSlots(plugin.configYml, "categories-gui")
        }
    }

    fun open(player: Player): Boolean {
        val categoriesMenu = menu ?: return false
        categoriesMenu.open(player)
        return true
    }
}
