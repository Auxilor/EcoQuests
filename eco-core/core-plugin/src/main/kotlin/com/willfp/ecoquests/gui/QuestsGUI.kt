package com.willfp.ecoquests.gui

import com.willfp.eco.core.gui.addCustomSlots
import com.willfp.eco.core.gui.addPageChanger
import com.willfp.eco.core.gui.component.ConfigButton
import com.willfp.eco.core.gui.component.addComponent
import com.willfp.eco.core.gui.component.closeButton
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.setMask
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.ecoquests.categories.Categories
import com.willfp.ecoquests.categories.Category
import com.willfp.ecoquests.gui.components.QuestAreaComponent
import com.willfp.ecoquests.gui.components.QuestInfoComponent
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object QuestsGUI {
    private class QuestMenu(
        val menu: Menu,
        val layout: QuestLayout
    )

    @Volatile
    private lateinit var allQuestsMenu: QuestMenu

    @Volatile
    private var categoryMenus: Map<String, QuestMenu> = emptyMap()

    private val lastCategory = ConcurrentHashMap<UUID, String>()

    fun reload() {
        CategoriesGUI.reload()
        lastCategory.clear()

        allQuestsMenu = buildMenu(null)
        categoryMenus = Categories.values().associate { it.id to buildMenu(it) }
    }

    private fun buildMenu(category: Category?): QuestMenu {
        val questLayout = QuestLayout(
            plugin.configYml.getSubsection("gui.quest-area"),
            allQuests = { Quests.values().filter { category == null || it.category == category } },
            getVisibleQuests = { player ->
                Quests.getShownQuests(player).filter { category == null || it.category == category }
            }
        )

        val questAreaComponent = QuestAreaComponent(
            plugin.configYml.getSubsection("gui.quest-area"),
            questLayout
        )

        val pageChangeSound = PlayableSound.create(plugin.configYml.getSubsection("gui.page-change-sound"))

        val menu = menu(plugin.configYml.getInt("gui.rows")) {
            title = if (category == null) {
                plugin.configYml.getFormattedString("gui.title")
            } else {
                (plugin.configYml.getFormattedStringOrNull("gui.category-title")
                    ?: plugin.configYml.getFormattedString("gui.title"))
                    .replace(oldValue = "%category%", newValue = category.name)
            }

            maxPages { questAreaComponent.getPages(it) }

            setMask(plugin.configYml, "gui")

            addComponent(QuestInfoComponent(plugin.configYml.getSubsection("gui.quest-info")))

            addComponent(closeButton(plugin.configYml.getSubsection("gui.close")))

            if (category != null && plugin.configYml.getBool("categories-gui.enabled")) {
                addComponent(ConfigButton(plugin.configYml.getSubsection("gui.back")) { player, _, _, _ ->
                    open(player)
                })
            }

            addPageChanger(plugin.configYml, "gui.prev-page", PageChanger.Direction.BACKWARDS, pageChangeSound)

            addPageChanger(plugin.configYml, "gui.next-page", PageChanger.Direction.FORWARDS, pageChangeSound)

            addComponent(questAreaComponent)

            addCustomSlots(plugin.configYml, "gui")
        }

        return QuestMenu(menu, questLayout)
    }

    /**
     * Open the main quests menu: the category menu when categories-gui is enabled,
     * otherwise the list of every quest.
     */
    fun open(player: Player) {
        lastCategory.remove(player.uniqueId)

        if (CategoriesGUI.open(player)) {
            return
        }

        allQuestsMenu.layout.invalidate(player)
        allQuestsMenu.menu.open(player)
    }

    /**
     * Open the quests menu for one category.
     */
    fun open(player: Player, category: Category) {
        val questMenu = categoryMenus[category.id] ?: return

        lastCategory[player.uniqueId] = category.id
        questMenu.layout.invalidate(player)
        questMenu.menu.open(player)
    }

    /**
     * Open the menu the player last opened: a category's quests, or the main menu.
     */
    fun reopen(player: Player) {
        val category = lastCategory[player.uniqueId]?.let { Categories[it] }

        if (category == null) {
            open(player)
        } else {
            open(player, category)
        }
    }

    fun invalidateLayout(player: Player) {
        allQuestsMenu.layout.invalidate(player)

        for (questMenu in categoryMenus.values) {
            questMenu.layout.invalidate(player)
        }
    }
}
