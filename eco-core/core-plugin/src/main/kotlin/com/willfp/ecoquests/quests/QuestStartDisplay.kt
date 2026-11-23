package com.willfp.ecoquests.quests

import com.willfp.ecoquests.api.event.PlayerQuestStartEvent
import com.willfp.ecoquests.plugin
import net.kyori.adventure.title.Title
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import wiki.ecohub.game.entity.player.asAudience
import wiki.ecohub.game.sound.PlayableSound
import wiki.ecohub.ui.text.toComponent
import java.time.Duration

object QuestStartDisplay : Listener {
    private val sound = PlayableSound.create(
        plugin.configYml.getSubsection("quests.start.sound")
    )

    @EventHandler
    fun handle(event: PlayerQuestStartEvent) {
        val player = event.player
        val quest = event.quest

        if (!quest.announcesStart) {
            return
        }

        if (plugin.configYml.getBool("quests.start.message.enabled")) {
            val rawMessage = plugin.configYml.getStrings("quests.start.message.message")

            val formatted = quest.addPlaceholdersInto(
                rawMessage,
                player
            )

            formatted.forEach { player.sendMessage(it) }
        }

        if (plugin.configYml.getBool("quests.start.title.enabled")) {
            val rawTitle = plugin.configYml.getString("quests.start.title.title")
            val rawSubtitle = plugin.configYml.getString("quests.start.title.subtitle")

            val formatted = quest.addPlaceholdersInto(
                listOf(rawTitle, rawSubtitle),
                player
            )

            player.asAudience().showTitle(
                Title.title(
                    formatted[0].toComponent(),
                    formatted[1].toComponent(),
                    Title.Times.times(
                        Duration.ofMillis((plugin.configYml.getDouble("quests.start.title.fade-in") * 1000).toLong()),
                        Duration.ofMillis((plugin.configYml.getDouble("quests.start.title.stay") * 1000).toLong()),
                        Duration.ofMillis((plugin.configYml.getDouble("quests.start.title.fade-out") * 1000).toLong())
                    )
                )
            )
        }

        sound?.playTo(player)
    }
}
