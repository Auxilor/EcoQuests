package com.willfp.ecoquests.libreforge

import com.willfp.ecoquests.categories.Categories
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get

object ConditionHasCompletedCategory : Condition<NoCompileData>("has_completed_category") {
    override val description = "Passes when the player has completed all quests in the specified category."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "category",
            "You must specify the category ID!",
            description = "The ID of the category to check.",
            type = ArgType.STRING,
            example = "woodcutting"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val category = Categories[config.getString("category")] ?: return false
        return category.isCompleted(player)
    }
}
