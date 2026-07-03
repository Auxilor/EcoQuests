package com.willfp.ecoquests.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoquests.categories.Categories
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionHasCompletedCategory : Condition<NoCompileData>("has_completed_category") {
    override val description = "Passes when the player has completed all quests in the specified category."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "category",
            "You must specify the category ID!",
            description = "The ID of the category to check.",
            type = ArgType.STRING
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
