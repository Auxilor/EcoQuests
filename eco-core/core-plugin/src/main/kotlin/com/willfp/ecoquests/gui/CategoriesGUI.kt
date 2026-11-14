package com.willfp.ecoquests.gui

import com.willfp.ecoquests.categories.Categories
import com.willfp.ecoquests.gui.components.CategoryAreaComponent
import com.willfp.ecoquests.gui.components.QuestInfoComponent
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player
import wiki.ecohub.modules.menus.addCustomSlots
import wiki.ecohub.modules.menus.component.ConfigButton
import wiki.ecohub.modules.menus.component.addComponent
import wiki.ecohub.modules.menus.component.closeButton
import wiki.ecohub.modules.menus.menu
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.modules.menus.setMask
import wiki.ecohub.modules.menus.slot.getSlotArea

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
            setTitle(plugin.configYml.getFormattedString("categories-gui.title"))

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
