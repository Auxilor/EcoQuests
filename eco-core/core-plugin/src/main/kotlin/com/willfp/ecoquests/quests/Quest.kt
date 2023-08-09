package com.willfp.ecoquests.quests

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString
import com.willfp.ecoquests.api.event.PlayerQuestCompleteEvent
import com.willfp.ecoquests.api.event.PlayerQuestStartEvent
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.ecoquests.util.formatDuration
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Quest(
    private val plugin: EcoPlugin,
    override val id: String,
    val config: Config
) : KRegistrable {
    val name = config.getFormattedString("name")

    val announcesStart = config.getBool("announce-start")

    private val guiItem = Items.lookup(config.getString("gui.item")).item

    val slot = slot({ player, _ ->
        guiItem.clone().modify {
            addLoreLines(
                addPlaceholdersInto(
                    plugin.configYml.getStrings("quests.icon.lore"),
                    player
                )
            )

            addLoreLines(
                startConditions.getNotMetLines(player, EmptyProvidedHolder)
            )
        }
    }) {

    }

    val showsInGui = config.getBool("gui.enabled")

    val alwaysInGUI = config.getBool("gui.always")

    val tasks = config.getStrings("tasks").mapNotNull { Tasks[it] }

    private val hasStartedKey: PersistentDataKey<Boolean> = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_has_started"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    private val hasCompletedKey: PersistentDataKey<Boolean> = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_has_completed"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    private val rewardMessages = config.getStrings("reward-messages")

    private val rewards = Effects.compileChain(
        config.getSubsections("rewards"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "quest $id rewards")
    )

    private val startEffects = Effects.compileChain(
        config.getSubsections("start-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "quest $id start-effects")
    )

    private val startConditions = Conditions.compile(
        config.getSubsections("start-conditions"),
        ViolationContext(plugin, "quest $id start-conditions")
    )

    private val lastResetTimeKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_last_reset_time"),
        PersistentDataKeyType.INT,
        0
    )

    private val resetTime = config.getInt("reset-time")

    val minutesUntilReset: Int
        get() = if (resetTime < 0) {
            Int.MAX_VALUE
        } else {
            val currentTime = (System.currentTimeMillis() / 1000 / 60).toInt()
            val previousTime = ServerProfile.load().read(lastResetTimeKey)

            resetTime - currentTime + previousTime
        }

    init {
        PlayerlessPlaceholder(plugin, "quest_${id}_name") {
            this.name
        }.register()

        PlayerPlaceholder(plugin, "quest_${id}_description") {
            this.getDescription(it).joinToString(" ")
        }.register()

        PlayerlessPlaceholder(plugin, "quest_${id}_tasks") {
            this.tasks.size.toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "quest_${id}_started") {
            hasStarted(it).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "quest_${id}_completed") {
            hasCompleted(it).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "quest_${id}_tasks_completed") {
            this.tasks.count { t -> t.hasCompleted(it) }.toNiceString()
        }.register()

        PlayerlessPlaceholder(plugin, "quest_${id}_time_until_reset") {
            formatDuration(this.minutesUntilReset)
        }.register()
    }

    fun getDescription(player: Player): List<String> {
        return addPlaceholdersInto(listOf(config.getString("description")), player)
    }

    fun hasActive(player: Player): Boolean {
        return hasStarted(player) && !hasCompleted(player)
    }

    fun hasCompleted(player: Player): Boolean {
        return player.profile.read(hasCompletedKey)
    }

    fun meetsStartConditions(player: Player): Boolean {
        return startConditions.areMet(player, EmptyProvidedHolder)
    }

    fun shouldStart(player: Player): Boolean {
        return meetsStartConditions(player) && !hasStarted(player)
    }

    fun hasStarted(player: Player): Boolean {
        return player.profile.read(hasStartedKey)
    }

    fun reset(player: Player) {
        player.profile.write(hasStartedKey, false)
        player.profile.write(hasCompletedKey, false)

        for (task in tasks) {
            task.reset(player)
        }
    }

    fun start(player: Player) {
        if (hasStarted(player)) {
            return
        }

        startEffects?.trigger(player)
        player.profile.write(hasStartedKey, true)

        Bukkit.getPluginManager().callEvent(PlayerQuestStartEvent(player, this))
    }

    fun resetIfNeeded() {
        if (resetTime < 0) {
            return
        }

        if (minutesUntilReset > 0) {
            return
        }

        ServerProfile.load().write(lastResetTimeKey, (System.currentTimeMillis() / 1000 / 60).toInt())

        for (player in Bukkit.getOnlinePlayers()) {
            reset(player)
        }
    }

    fun checkCompletion(player: Player): Boolean {
        // Check if the player has completed the Quest before
        if (player.profile.read(hasCompletedKey)) {
            return true
        }

        if (tasks.all { it.hasCompleted(player) }) {
            player.profile.write(hasCompletedKey, true)
            rewards?.trigger(player)

            Bukkit.getPluginManager().callEvent(PlayerQuestCompleteEvent(player, this))

            return true
        }

        return false
    }

    private fun List<String>.addMargin(margin: Int): List<String> {
        return this.map { s -> " ".repeat(margin) + s }
    }

    fun addPlaceholdersInto(
        strings: List<String>,
        player: Player
    ): List<String> {
        val quest = this // I just hate the @ notation kotlin uses
        fun String.addPlaceholders() = this
            .replace("%quest%", quest.name)
            .replace("%time_until_reset%", formatDuration(quest.minutesUntilReset))

        // Replace multi-line placeholders.
        val processed = strings.flatMap { s ->
            val margin = s.length - s.trimStart().length

            if (s.contains("%rewards%")) {
                rewardMessages
                    .addMargin(margin)
            } else if (s.contains("%tasks%")) {
                tasks.flatMap { task -> task.getCompletedDescription(player) }
                    .addMargin(margin)
            } else if (s.contains("%description%")) {
                getDescription(player)
                    .addMargin(margin)
            } else {
                listOf(s)
            }
        }.map { it.addPlaceholders() }

        return processed.formatEco(
            placeholderContext(
                player = player
            )
        )
    }
}
