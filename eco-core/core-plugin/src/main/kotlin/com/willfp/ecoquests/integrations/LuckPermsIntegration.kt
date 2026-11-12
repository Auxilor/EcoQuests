package com.willfp.ecoquests.integrations

import com.willfp.ecoquests.quests.MaxActiveQuests
import net.luckperms.api.LuckPermsProvider
import org.bukkit.entity.Player
import wiki.ecohub.core.plugin.BaseEcoPlugin
import wiki.ecohub.provider.ExternalProvider

object LuckPermsIntegration : ExternalProvider {
    override fun onLoad(plugin: BaseEcoPlugin) {
        val playerAdapter = LuckPermsProvider.get().getPlayerAdapter(Player::class.java)

        MaxActiveQuests.permissionsVersion = { playerAdapter.getPermissionData(it) }
    }
}
