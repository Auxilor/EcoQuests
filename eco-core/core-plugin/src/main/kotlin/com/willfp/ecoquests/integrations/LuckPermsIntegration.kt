package com.willfp.ecoquests.integrations

import com.willfp.ecoquests.quests.MaxActiveQuests
import net.luckperms.api.LuckPermsProvider
import org.bukkit.entity.Player

object LuckPermsIntegration {
    fun load() {
        val playerAdapter = LuckPermsProvider.get().getPlayerAdapter(Player::class.java)

        MaxActiveQuests.permissionsVersion = { playerAdapter.getPermissionData(it) }
    }
}
