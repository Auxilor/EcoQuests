package com.willfp.ecoquests.quests

import com.willfp.ecoquests.api.event.PlayerQuestCompleteEvent
import com.willfp.ecoquests.api.event.PlayerQuestStartEvent
import com.willfp.ecoquests.categories.Categories
import com.willfp.ecoquests.categories.Category
import com.willfp.ecoquests.gui.QuestsGUI
import com.willfp.ecoquests.tasks.Task
import com.willfp.ecoquests.tasks.TaskTemplate
import com.willfp.ecoquests.tasks.Tasks
import com.willfp.ecoquests.util.formatDuration
import com.willfp.ecoquests.util.randomlyPick
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.KRegistrable
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.data.PersistentDataKey
import wiki.ecohub.data.PersistentDataKeyType
import wiki.ecohub.data.profile.ServerProfile
import wiki.ecohub.game.entity.player.profile
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.game.item.Items
import wiki.ecohub.game.item.modify
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.modules.menus.onLeftClick
import wiki.ecohub.modules.menus.slot
import wiki.ecohub.rules.chain.NormalExecutorFactory
import wiki.ecohub.rules.condition.Conditions
import wiki.ecohub.rules.effect.Effects
import wiki.ecohub.rules.holder.EmptyProvidedHolder
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.placeholder.type.PlayerPlaceholder
import wiki.ecohub.ui.placeholder.type.PlayerlessPlaceholder
import wiki.ecohub.ui.text.formatEco
import wiki.ecohub.ui.text.lineWrap
import java.time.Instant

val currentTimeMinutes: Int
    get() = (System.currentTimeMillis() / 1000 / 60).toInt()

class Quest(
    private val plugin: EcoPlugin,
    override val id: String,
    val config: Config
) : KRegistrable {
    val name = config.getFormattedString("name")

    val category: Category? = runCatching { config.getString("category") }
        .getOrElse { "" }
        .takeIf { it.isNotBlank() }
        ?.let { categoryId ->
            Categories[categoryId].also { cat ->
                if (cat == null) {
                    plugin.logger.warning("Quest $id references unknown category '$categoryId'")
                }
            }
        }

    val announcesStart = config.getBool("announce-start")

    val autoStarts = config.getBool("auto-start")

    private val guiItem = Items.lookup(config.getString("gui.item")).getItem()

    val slot = slot({ player, _ ->
        guiItem.clone().modify {
            addLoreLines(
                addPlaceholdersInto(
                    plugin.configYml.getStrings("quests.icon.lore"),
                    player
                )
            )

            addLoreLines(
                startConditions.getNotMetLines(player.toDispatcher(), EmptyProvidedHolder)
            )

            if (
                plugin.configYml.getBool("gui.click-to-start") &&
                isStartableBy(player)
            ) {
                addLoreLines(
                    addPlaceholdersInto(
                        plugin.configYml.getStrings("quests.icon.click-to-start-lore"),
                        player
                    )
                )
            }

            setDisplayName(
                addPlaceholdersInto(
                    listOf(plugin.configYml.getString("quests.icon.name")),
                    player
                ).first()
            )
        }
    }) {
        onLeftClick { player, _, _, menu ->
            tryStartFromGui(player, menu)
        }
    }

    val showsInGui = config.getBool("gui.enabled")

    val alwaysInGUI = config.getBool("gui.always")

    val hidesLocked = config.getBoolOrNull("gui.hide-locked") == true

    // Absent = auto-placed. Coordinates are absolute menu coords; validated
    // against gui.quest-area bounds once at GUI reload (see QuestLayout).
    val position: QuestPosition? = config.getSubsectionOrNull("gui.position")?.let {
        QuestPosition(it.getInt("page"), it.getInt("row"), it.getInt("column"))
    }

    // NEW — keep the quest visible in the main GUI after completion, in its
    // normal slot, instead of only appearing in PreviousQuestsGUI.
    val showsCompleted: Boolean = config.getBoolOrNull("gui.show-completed") ?: false

    private val lastResetTimeKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_last_reset_time"),
        PersistentDataKeyType.INT,
        0
    )

    private val resetEpochKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_reset_epoch"),
        PersistentDataKeyType.INT,
        0
    )

    private val resetTime = config.getInt("reset-time")

    private val fixedResetSchedule = config.getSubsectionOrNull("reset-schedule")?.let { scheduleConfig ->
        runCatching {
            FixedResetSchedule.parse(
                type = scheduleConfig.getString("type"),
                time = scheduleConfig.getString("time"),
                day = runCatching { scheduleConfig.getString("day") }.getOrNull(),
                timezone = runCatching { scheduleConfig.getString("timezone") }.getOrNull()
            )
        }.onFailure { error ->
            plugin.logger.warning("Quest $id has an invalid reset-schedule: ${error.message}. Falling back to reset-time.")
        }.getOrNull()
    }

    val isResettable: Boolean
        get() = fixedResetSchedule != null || resetTime >= 0

    val minutesUntilReset: Int
        get() {
            fixedResetSchedule?.let { schedule ->
                return (schedule.nextResetMinute(Instant.now()) - currentTimeMinutes)
                    .coerceAtMost(Int.MAX_VALUE.toLong())
                    .toInt()
            }

            return if (resetTime < 0) {
                Int.MAX_VALUE
            } else {
                val previousTime = ServerProfile.load().read(lastResetTimeKey)

                resetTime - currentTimeMinutes + previousTime
            }
        }

    // The pool of available tasks to pick from
    private val availableTasks = config.getSubsections("tasks")
        .mapNotNull {
            Tasks[it.getString("task")]
                ?.create(this, it.getString("xp"))
        }

    // The amount of tasks to use from the pool
    val taskAmount = config.getInt("task-amount").let {
        if (it < 0) {
            availableTasks.size
        } else {
            it.coerceAtMost(availableTasks.size)
        }
    }

    private val savedTasksKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_tasks"),
        PersistentDataKeyType.STRING_LIST,
        emptyList()
    )

    // The tasks that are actually in use
    @Volatile
    var tasks = run {
        if (taskAmount == availableTasks.size) {
            // If taskAmount is equal to availableTasks.size then tasks are ordered as configured
            availableTasks.take(taskAmount)
        } else {
            // If taskAmount is less than availableTasks.size then tasks are selected and ordered randomly,
            // and the selection is kept until the quest resets.
            loadTasks()?.takeIf { isResettable || it.isInPool() }
                ?: availableTasks.randomlyPick(taskAmount).also { saveTasks(it) }
        }
    }
        private set

    private val hasStartedKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_has_started"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    private val startedTimeKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_started_time"),
        PersistentDataKeyType.INT,
        Int.MAX_VALUE
    )

    private val startedEpochKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_started_epoch"),
        PersistentDataKeyType.INT,
        0
    )

    private val hasCompletedKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_has_completed"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    private val completedTimeKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_completed_time"),
        PersistentDataKeyType.INT,
        Int.MAX_VALUE
    )

    private val completedEpochKey = PersistentDataKey(
        plugin.createNamespacedKey("quest_${id}_completed_epoch"),
        PersistentDataKeyType.INT,
        0
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

        PlayerPlaceholder(plugin, "quest_${id}_time_since_start") {
            if (hasStarted(it)) formatDuration(this.getTimeSinceStart(it)) else ""
        }.register()

        PlayerPlaceholder(plugin, "quest_${id}_time_since_completed") {
            if (hasCompleted(it)) formatDuration(this.getTimeSinceCompletion(it)) else ""
        }.register()

        PlayerPlaceholder(plugin, "quest_${id}_time_since") {
            if (hasCompleted(it)) {
                plugin.langYml.getString("time-since.completed")
                    .replace("%time%", formatDuration(this.getTimeSinceCompletion(it)))
                    .formatEco(it)
            } else if (hasStarted(it)) {
                plugin.langYml.getString("time-since.started")
                    .replace("%time%", formatDuration(this.getTimeSinceStart(it)))
                    .formatEco(it)
            } else {
                plugin.langYml.getString("time-since.never")
                    .formatEco(it)
            }
        }.register()

        for (i in 1..this.tasks.size) {
            PlayerPlaceholder(plugin, "quest_${id}_task_${i}_required_xp") {
                this.tasks[i-1].getExperienceRequired(it).toNiceString()
            }.register()

            PlayerPlaceholder(plugin, "quest_${id}_task_${i}_xp") {
                this.tasks[i-1].getExperience(it).toNiceString()
            }.register()

            PlayerPlaceholder(plugin, "quest_${id}_task_${i}_description") {
                this.tasks[i-1].getDescription(it)
            }.register()

            PlayerPlaceholder(plugin, "quest_${id}_task_${i}_completed") {
                this.tasks[i-1].hasCompleted(it).toNiceString()
            }.register()

            PlayerPlaceholder(plugin, "quest_${id}_task_${i}_completed_description") {
                this.tasks[i-1].getCompletedDescription(it)
            }.register()
        }
    }

    override fun onRegister() {
        for (task in tasks) {
            task.bind()
        }
    }

    override fun onRemove() {
        saveTasks()

        for (task in tasks) {
            task.unbind()
        }
    }

    fun getTask(template: TaskTemplate): Task? {
        return tasks.firstOrNull { it.template == template }
    }

    fun getDescription(player: Player): List<String> {
        val descriptions = config.getStrings("description")
        return addPlaceholdersInto(descriptions, player)
    }

    fun hasActive(player: OfflinePlayer): Boolean {
        return hasStarted(player) && !hasCompleted(player)
    }

    // Progress from an older epoch is stale, so a reset doesn't have to clear it
    // player by player. See reset().
    private val resetEpoch: Int
        get() = ServerProfile.load().read(resetEpochKey)

    fun hasCompleted(player: OfflinePlayer): Boolean {
        return player.profile.read(hasCompletedKey)
            && player.profile.read(completedEpochKey) == resetEpoch
    }

    fun isUnlockedFor(player: Player): Boolean =
        !hidesLocked || meetsStartConditions(player)

    fun meetsStartConditions(player: Player): Boolean {
        return startConditions.areMet(player.toDispatcher(), EmptyProvidedHolder)
    }

    fun shouldStart(player: Player): Boolean =
        autoStarts && !hasStarted(player) && meetsStartConditions(player)

    fun hasStarted(player: OfflinePlayer): Boolean {
        return player.profile.read(hasStartedKey)
            && player.profile.read(startedEpochKey) == resetEpoch
    }

    fun reset(player: OfflinePlayer) {
        player.profile.write(hasStartedKey, false)
        player.profile.write(hasCompletedKey, false)

        for (task in tasks) {
            task.reset(player)
        }
    }

    fun isStartableBy(player: Player): Boolean {
        return !hasCompleted(player) && !hasStarted(player) && meetsStartConditions(player)
            && !Quests.hasReachedMaxActiveQuests(player)
    }

    fun tryStartFromGui(player: Player, menu: Menu) {
        if (!plugin.configYml.getBool("gui.click-to-start")) {
            return
        }

        if (hasCompleted(player)) {
            player.sendMessage(plugin.langYml.getMessage("cannot-start-completed"))
            return
        }

        if (hasStarted(player)) {
            player.sendMessage(plugin.langYml.getMessage("already-started"))
            return
        }

        if (!meetsStartConditions(player)) {
            player.sendMessage(plugin.langYml.getMessage("cannot-start-conditions"))
            return
        }

        if (Quests.hasReachedMaxActiveQuests(player)) {
            player.sendMessage(plugin.langYml.getMessage("max-active-quests"))
            return
        }

        start(player)

        player.sendMessage(
            plugin.langYml.getMessage("started-quest-self")
                .replace("%quest%", name)
        )

        QuestsGUI.invalidateLayout(player)
        menu.refresh(player)
    }

    fun start(player: Player) {
        if (hasStarted(player) || hasCompleted(player)) {
            return
        }

        startEffects?.trigger(player.toDispatcher())
        player.profile.write(hasStartedKey, true)
        player.profile.write(startedTimeKey, currentTimeMinutes)
        player.profile.write(startedEpochKey, resetEpoch)

        // Reset tasks to generate new xp requirements
        for (task in tasks) {
            task.reset(player)
        }

        Bukkit.getPluginManager().callEvent(PlayerQuestStartEvent(player, this))
    }

    fun getTimeSinceStart(player: OfflinePlayer): Int {
        return currentTimeMinutes - player.profile.read(startedTimeKey)
    }

    fun getTimeSinceCompletion(player: OfflinePlayer): Int {
        return currentTimeMinutes - player.profile.read(completedTimeKey)
    }

    fun getTimeSincePlaceholder(player: Player): String {
        return if (hasCompleted(player)) {
            plugin.langYml.getString("time-since.completed")
                .replace("%time%", formatDuration(this.getTimeSinceCompletion(player)))
                .formatEco(player)
        } else if (hasStarted(player)) {
            plugin.langYml.getString("time-since.started")
                .replace("%time%", formatDuration(this.getTimeSinceStart(player)))
                .formatEco(player)
        } else {
            plugin.langYml.getString("time-since.never")
                .formatEco(player)
        }
    }

    fun resetIfNeeded() {
        if (!isResettable) {
            return
        }

        val lastResetTime = ServerProfile.load().read(lastResetTimeKey)
        val shouldReset = fixedResetSchedule?.let { schedule ->
            schedule.previousResetMinute(Instant.now()) > lastResetTime
        } ?: (minutesUntilReset <= 0)

        if (!shouldReset) {
            return
        }

        reset()
    }

    fun reset() {
        // Bumping the epoch invalidates every player's progress at once; each player's
        // own keys are rewritten lazily, when they next start the quest. Clearing them here
        // instead meant one database transaction per key per player on the entire roster,
        // online and offline, every time a quest reset. The time is kept for
        // minutesUntilReset, which still schedules off the clock.
        ServerProfile.load().write(lastResetTimeKey, currentTimeMinutes)
        ServerProfile.load().write(resetEpochKey, resetEpoch + 1)

        // Unbind old tasks
        for (task in tasks) {
            task.unbind()
        }

        tasks = availableTasks.randomlyPick(taskAmount)

        // Bind new tasks
        for (task in tasks) {
            task.bind()
        }

        // Save new tasks
        saveTasks()
    }

    private fun loadTasks(): List<Task>? {
        val serialized = ServerProfile.load().read(savedTasksKey)

        if (serialized.isEmpty()) {
            return null
        }

        val savedTasks = mutableListOf<Task>()

        for (s in serialized) {
            val split = s.split(":", limit = 2)
            if (split.size != 2) {
                plugin.logger.warning("Quest $id has a malformed saved task '$s', skipping it.")
                continue
            }
            val taskId = split[0]
            val xpExpr = split[1]

            val template = Tasks[taskId] ?: continue

            savedTasks += template.create(this, xpExpr)
        }

        return savedTasks
    }

    private fun List<Task>.isInPool(): Boolean {
        return size == taskAmount && all { task ->
            availableTasks.any { it.template == task.template && it.xpExpr == task.xpExpr }
        }
    }

    private fun saveTasks(tasks: List<Task> = this.tasks) {
        val serialized = tasks.map {
            "${it.template.id}:${it.xpExpr}"
        }

        ServerProfile.load().write(savedTasksKey, serialized)
    }

    fun checkCompletion(player: Player): Boolean {
        // Check if the player has completed the Quest before
        if (hasCompleted(player)) {
            return true
        }

        if (tasks.all { it.hasCompleted(player) }) {
            complete(player)
            return true
        }

        return false
    }

    private fun complete(player: Player) {
        player.profile.write(hasCompletedKey, true)
        player.profile.write(completedTimeKey, currentTimeMinutes)
        player.profile.write(completedEpochKey, resetEpoch)
        rewards?.trigger(player.toDispatcher())

        Bukkit.getPluginManager().callEvent(PlayerQuestCompleteEvent(player, this))
        category?.checkAndComplete(player)
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
            .replace("%category%", quest.category?.name ?: plugin.langYml.getString("no-category"))
            .replace("%time_until_reset%", formatDuration(quest.minutesUntilReset))
            .replace("%time_since%", getTimeSincePlaceholder(player))

        // Replace multi-line placeholders.
        val processed = strings.flatMap { s ->
            val margin = s.length - s.trimStart().length

            if (s.contains("%rewards%")) {
                rewardMessages
                    .addMargin(margin)
            } else if (s.contains("%tasks%")) {
                tasks.map { task -> task.getCompletedDescription(player) }
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
        ).lineWrap(plugin.configYml.getInt("quests.icon.line-wrap"), true)
    }
}
