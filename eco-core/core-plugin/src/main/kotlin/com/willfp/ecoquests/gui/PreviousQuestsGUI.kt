package com.willfp.ecoquests.gui

import com.willfp.eco.core.gui.addCustomSlots
import com.willfp.eco.core.gui.addPageChanger
import com.willfp.eco.core.gui.component.ConfigButton
import com.willfp.eco.core.gui.component.addComponent
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.setMask
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.ecoquests.gui.components.FlatListQuestSlotProvider
import com.willfp.ecoquests.gui.components.QuestAreaComponent
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player

object PreviousQuestsGUI {
    @Volatile
    private lateinit var menu: Menu

    fun reload() {
        val questAreaComponent = QuestAreaComponent(
            plugin.configYml.getSubsection("completed-gui.quest-area"),
            FlatListQuestSlotProvider { Quests.getShownCompletedQuests(it) }
        )

        val pageChangeSound = PlayableSound.create(plugin.configYml.getSubsection("completed-gui.page-change-sound"))

        menu = menu(plugin.configYml.getInt("completed-gui.rows")) {
            title = plugin.configYml.getFormattedString("completed-gui.title")

            maxPages { questAreaComponent.getPages(it) }

            setMask(plugin.configYml, "completed-gui")

            addComponent(ConfigButton(plugin.configYml.getSubsection("completed-gui.back")) { player, _, _, _ -> QuestsGUI.open(player) })

            addPageChanger(plugin.configYml, "completed-gui.prev-page", PageChanger.Direction.BACKWARDS, pageChangeSound)

            addPageChanger(plugin.configYml, "completed-gui.next-page", PageChanger.Direction.FORWARDS, pageChangeSound)

            addComponent(questAreaComponent)

            addCustomSlots(plugin.configYml, "completed-gui")

            onClose { event, _ ->
                plugin.scheduler.on(event.player).run {
                    QuestsGUI.open(event.player as Player)
                }
            }
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
