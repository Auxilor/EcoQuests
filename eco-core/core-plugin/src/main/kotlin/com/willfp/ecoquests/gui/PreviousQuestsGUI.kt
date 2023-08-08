package com.willfp.ecoquests.gui

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.ecoquests.gui.components.BackButton
import com.willfp.ecoquests.gui.components.CloseButton
import com.willfp.ecoquests.gui.components.QuestInfoComponent
import com.willfp.ecoquests.gui.components.PositionedPageChanger
import com.willfp.ecoquests.gui.components.QuestAreaComponent
import com.willfp.ecoquests.gui.components.addComponent
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player

object PreviousQuestsGUI {
    private lateinit var menu: Menu

    fun reload(plugin: EcoPlugin) {
        val questAreaComponent = QuestAreaComponent(plugin.configYml.getSubsection("completed-gui.quest-area")) {
            Quests.getCompletedQuests(it)
        }

        menu = menu(plugin.configYml.getInt("completed-gui.rows")) {
            title = plugin.configYml.getFormattedString("completed-gui.title")

            maxPages { questAreaComponent.getPages(it) }

            setMask(
                FillerMask(
                    MaskItems.fromItemNames(plugin.configYml.getStrings("completed-gui.mask.materials")),
                    *plugin.configYml.getStrings("completed-gui.mask.pattern").toTypedArray()
                )
            )

            addComponent(BackButton(plugin.configYml.getSubsection("completed-gui.back")))

            addComponent(
                PositionedPageChanger(
                    plugin.configYml.getSubsection("completed-gui.prev-page"),
                    PageChanger.Direction.BACKWARDS
                )
            )

            addComponent(
                PositionedPageChanger(
                    plugin.configYml.getSubsection("completed-gui.next-page"),
                    PageChanger.Direction.FORWARDS
                )
            )

            addComponent(questAreaComponent)

            for (config in plugin.configYml.getSubsections("completed-gui.custom-slots")) {
                setSlot(
                    config.getInt("row"),
                    config.getInt("column"),
                    ConfigSlot(config)
                )
            }

            onClose { event, _ ->
                plugin.scheduler.run {
                    QuestsGUI.open(event.player as Player)
                }
            }
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
