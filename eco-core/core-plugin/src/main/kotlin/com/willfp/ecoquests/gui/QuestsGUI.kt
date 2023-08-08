package com.willfp.ecoquests.gui

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.ecoquests.gui.components.CloseButton
import com.willfp.ecoquests.gui.components.QuestInfoComponent
import com.willfp.ecoquests.gui.components.PositionedPageChanger
import com.willfp.ecoquests.gui.components.QuestAreaComponent
import com.willfp.ecoquests.gui.components.addComponent
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player

object QuestsGUI {
    private lateinit var menu: Menu

    fun reload(plugin: EcoPlugin) {
        val questAreaComponent = QuestAreaComponent(plugin.configYml.getSubsection("gui.quest-area")) {
            Quests.getShownQuests(it)
        }

        menu = menu(plugin.configYml.getInt("gui.rows")) {
            title = plugin.configYml.getFormattedString("gui.title")

            maxPages { questAreaComponent.getPages(it) }

            setMask(
                FillerMask(
                    MaskItems.fromItemNames(plugin.configYml.getStrings("gui.mask.materials")),
                    *plugin.configYml.getStrings("gui.mask.pattern").toTypedArray()
                )
            )

            addComponent(QuestInfoComponent(plugin.configYml.getSubsection("gui.quest-info")))

            addComponent(CloseButton(plugin.configYml.getSubsection("gui.close")))

            addComponent(PositionedPageChanger(
                plugin.configYml.getSubsection("gui.prev-page"),
                PageChanger.Direction.BACKWARDS)
            )

            addComponent(PositionedPageChanger(
                plugin.configYml.getSubsection("gui.next-page"),
                PageChanger.Direction.FORWARDS)
            )

            addComponent(questAreaComponent)

            for (config in plugin.configYml.getSubsections("gui.custom-slots")) {
                setSlot(
                    config.getInt("row"),
                    config.getInt("column"),
                    ConfigSlot(config)
                )
            }
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
