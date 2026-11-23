package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.api.event.PlayerTaskExpGainEvent
import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.quests.Quests
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.effect.MultiMultiplierEffect

object EffectQuestXpMultiplier : MultiMultiplierEffect<Quest>("quest_xp_multiplier") {
    override val key = "quests"

    override fun getElement(key: String): Quest? {
        return Quests[key]
    }

    override fun getAllElements(): Collection<Quest> {
        return Quests.values()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerTaskExpGainEvent) {
        event.amount *= getMultiplier(event.player.toDispatcher(), event.quest)
    }
}
