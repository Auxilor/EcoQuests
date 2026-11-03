package com.willfp.ecoquests.gui

import com.willfp.eco.core.gui.addCustomSlots
import com.willfp.eco.core.gui.component.ConfigButton
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
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player

object CategoriesGUI {
    @Volatile
    private var menu: Menu? = null

    fun reload() {
        val categories = Categories.values().filter { it.showsInGui }.sortedBy { it.id }

        if (!plugin.configYml.getBool("categories-gui.enabled") || categories.isEmpty()) {
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
                    categories
                )
            )

            addComponent(
                ConfigButton(
                    plugin.configYml.getSubsection("categories-gui.uncategorised"),
                    enabledByDefault = Quests.values().any { it.showsInGui && it.category == null }
                ) { player, _, _, _ ->
                    QuestsGUI.openUncategorised(player)
                }
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
