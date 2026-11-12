package com.willfp.ecoquests.tasks

import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quest
import wiki.ecohub.config.Config
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.core.registry.KRegistrable
import wiki.ecohub.rules.counter.Counters
import wiki.ecohub.rules.effect.Effects

class TaskTemplate(
    override val id: String,
    val config: Config
) : KRegistrable {
    val xpGainMethods = config.getSubsections("xp-gain-methods").mapNotNull {
        Counters.compile(it, ViolationContext(plugin, "task $id tasks"))
    }

    val onComplete = Effects.compileChain(
        config.getSubsections("on-complete"),
        ViolationContext(plugin, "task $id on-complete")
    )

    fun create(quest: Quest, xpExpr: String) =
        Task(this, quest, xpExpr)
}
