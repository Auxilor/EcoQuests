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
import com.willfp.ecoquests.quests.Quest
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
    private lateinit var uncategorisedMenu: QuestMenu

    @Volatile
    private var categoryMenus: Map<String, QuestMenu> = emptyMap()

    private const val UNCATEGORISED = "#uncategorised"

    private val lastCategory = ConcurrentHashMap<UUID, String>()

    fun reload() {
        CategoriesGUI.reload()
        lastCategory.clear()

        allQuestsMenu = buildMenu(plugin.configYml.getFormattedString("gui.title"), hasBackButton = false) { true }

        uncategorisedMenu = buildMenu(
            categoryTitle(plugin.configYml.getFormattedString("categories-gui.uncategorised.category-name")),
            hasBackButton = true
        ) { it.category == null }

        categoryMenus = Categories.values().associate { category ->
            category.id to buildMenu(categoryTitle(category.name), hasBackButton = true) { it.category == category }
        }
    }

    private fun categoryTitle(categoryName: String): String =
        (plugin.configYml.getFormattedStringOrNull("gui.category-title")
            ?: plugin.configYml.getFormattedString("gui.title"))
            .replace(oldValue = "%category%", newValue = categoryName)

    private fun buildMenu(menuTitle: String, hasBackButton: Boolean, includes: (Quest) -> Boolean): QuestMenu {
        val questLayout = QuestLayout(
            plugin.configYml.getSubsection("gui.quest-area"),
            allQuests = { Quests.values().filter(includes) },
            getVisibleQuests = { player -> Quests.getShownQuests(player).filter(includes) }
        )

        val questAreaComponent = QuestAreaComponent(
            plugin.configYml.getSubsection("gui.quest-area"),
            questLayout
        )

        val pageChangeSound = PlayableSound.create(plugin.configYml.getSubsection("gui.page-change-sound"))

        val menu = menu(plugin.configYml.getInt("gui.rows")) {
            title = menuTitle

            maxPages { questAreaComponent.getPages(it) }

            setMask(plugin.configYml, "gui")

            addComponent(QuestInfoComponent(plugin.configYml.getSubsection("gui.quest-info")))

            addComponent(closeButton(plugin.configYml.getSubsection("gui.close")))

            if (hasBackButton && plugin.configYml.getBool("categories-gui.enabled")) {
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
     * Open the quests menu for quests with no category.
     */
    fun openUncategorised(player: Player) {
        lastCategory[player.uniqueId] = UNCATEGORISED
        uncategorisedMenu.layout.invalidate(player)
        uncategorisedMenu.menu.open(player)
    }

    /**
     * Open the menu the player last opened: a category's quests, or the main menu.
     */
    fun reopen(player: Player) {
        val lastOpened = lastCategory[player.uniqueId]

        if (lastOpened == UNCATEGORISED) {
            openUncategorised(player)
            return
        }

        val category = lastOpened?.let { Categories[it] }

        if (category == null) {
            open(player)
        } else {
            open(player, category)
        }
    }

    fun invalidateLayout(player: Player) {
        allQuestsMenu.layout.invalidate(player)
        uncategorisedMenu.layout.invalidate(player)

        for (questMenu in categoryMenus.values) {
            questMenu.layout.invalidate(player)
        }
    }
}
