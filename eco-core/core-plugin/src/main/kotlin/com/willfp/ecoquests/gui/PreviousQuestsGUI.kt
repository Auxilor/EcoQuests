package com.willfp.ecoquests.gui

import com.willfp.ecoquests.gui.components.FlatListQuestSlotProvider
import com.willfp.ecoquests.gui.components.QuestAreaComponent
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quests
import org.bukkit.entity.Player
import wiki.ecohub.game.sound.PlayableSound
import wiki.ecohub.modules.menus.addCustomSlots
import wiki.ecohub.modules.menus.addPageChanger
import wiki.ecohub.modules.menus.component.ConfigButton
import wiki.ecohub.modules.menus.component.addComponent
import wiki.ecohub.modules.menus.menu
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.modules.menus.page.PageChanger
import wiki.ecohub.modules.menus.setMask

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
            setTitle(plugin.configYml.getFormattedString("completed-gui.title"))

            maxPages { questAreaComponent.getPages(it) }

            setMask(plugin.configYml, "completed-gui")

            addComponent(ConfigButton(plugin.configYml.getSubsection("completed-gui.back")) { player, _, _, _ ->
                QuestsGUI.reopen(player)
            })

            addPageChanger(plugin.configYml, "completed-gui.prev-page", PageChanger.Direction.BACKWARDS, pageChangeSound)

            addPageChanger(plugin.configYml, "completed-gui.next-page", PageChanger.Direction.FORWARDS, pageChangeSound)

            addComponent(questAreaComponent)

            addCustomSlots(plugin.configYml, "completed-gui")

            onClose { event, _ ->
                plugin.scheduler.on(event.player).run {
                    QuestsGUI.reopen(event.player as Player)
                }
            }
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
