package com.willfp.ecoquests.gui

import com.willfp.eco.core.gui.addPageChanger
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.ecoquests.gui.components.CloseButton
import com.willfp.ecoquests.gui.components.QuestInfoComponent
import com.willfp.ecoquests.gui.components.QuestAreaComponent
import com.willfp.ecoquests.gui.components.addComponent
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player

object QuestsGUI {
    private lateinit var menu: Menu

    fun reload() {
        val questAreaComponent = QuestAreaComponent(plugin.configYml.getSubsection("gui.quest-area")) {
            Quests.getShownQuests(it)
        }

        val pageChangeSound = PlayableSound.create(plugin.configYml.getSubsection("gui.page-change-sound"))

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

            addPageChanger(plugin.configYml, "gui.prev-page", PageChanger.Direction.BACKWARDS, pageChangeSound)

            addPageChanger(plugin.configYml, "gui.next-page", PageChanger.Direction.FORWARDS, pageChangeSound)

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
