package com.willfp.ecoquests.gui

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.MenuUtils
import com.willfp.ecoquests.gui.components.QuestSlotProvider
import com.willfp.ecoquests.plugin
import com.willfp.ecoquests.quests.Quest
import org.bukkit.entity.Player
import java.util.UUID

/**
 * Merges positioned quests (gui.position) and auto-placed quests into a
 * single per-player grid. Position validity (bounds/collisions) is computed
 * once, at construction, against all quests regardless of visibility.
 * Per-player visibility and page contents are computed lazily and cached
 * until [invalidate] is called.
 */
class QuestLayout(
    areaConfig: Config,
    allQuests: () -> List<Quest>,
    private val getVisibleQuests: (Player) -> List<Quest>
) : QuestSlotProvider {
    private val topLeftRow = areaConfig.getInt("top-left.row")
    private val topLeftColumn = areaConfig.getInt("top-left.column")
    private val rowSize = areaConfig.getInt("bottom-right.row") - topLeftRow + 1
    private val columnSize = areaConfig.getInt("bottom-right.column") - topLeftColumn + 1
    private val pageSize = rowSize * columnSize

    init {
        if (pageSize <= 0) {
            plugin.logger.warning(
                "gui.quest-area has a non-positive size (rowSize=$rowSize, columnSize=$columnSize) - " +
                    "check that bottom-right is below/right of top-left in config.yml. " +
                    "The quest GUI will show no quests until this is fixed."
            )
        }
    }

    // questId -> (page, localRow, localColumn), only for quests whose configured
    // gui.position passed bounds/collision validation.
    private val validatedPositions: Map<String, Triple<Int, Int, Int>> = run {
        val result = mutableMapOf<String, Triple<Int, Int, Int>>()
        val seen = mutableMapOf<Triple<Int, Int, Int>, String>()

        for (quest in allQuests()) {
            val pos = quest.position ?: continue

            val localRow = pos.row - topLeftRow + 1
            val localColumn = pos.column - topLeftColumn + 1

            if (pos.page < 1 || localRow !in 1..rowSize || localColumn !in 1..columnSize) {
                plugin.logger.warning(
                    "Quest '${quest.id}' has gui.position (page ${pos.page}, row ${pos.row}, " +
                        "column ${pos.column}) outside gui.quest-area bounds; " +
                        "falling back to automatic placement."
                )
                continue
            }

            val key = Triple(pos.page, localRow, localColumn)
            val existing = seen[key]

            if (existing != null) {
                plugin.logger.warning(
                    "Quest '${quest.id}' has gui.position (page ${pos.page}, row ${pos.row}, " +
                        "column ${pos.column}) colliding with quest '$existing'; " +
                        "falling back to automatic placement."
                )
                continue
            }

            seen[key] = quest.id
            result[quest.id] = key
        }

        result
    }

    // Cache of the full per-page grid for a player, valid until invalidate() is called.
    private val cache = mutableMapOf<UUID, List<Map<Int, Quest>>>()

    private fun buildPages(player: Player): List<Map<Int, Quest>> {
        if (pageSize <= 0) {
            // Invalid gui.quest-area config - already warned in init. Bail out
            // instead of looping forever trying to drain the auto-placement
            // queue into a page that can never hold any slots.
            return listOf(emptyMap())
        }

        val visible = getVisibleQuests(player)
        val positionedVisible = visible.filter { validatedPositions.containsKey(it.id) }
        val autoQueue = ArrayDeque(visible.filterNot { validatedPositions.containsKey(it.id) })

        val positionedByPage = positionedVisible.groupBy { validatedPositions.getValue(it.id).first }
        val maxPositionedPage = positionedByPage.keys.maxOrNull() ?: 1

        val pages = mutableListOf<Map<Int, Quest>>()
        var page = 1

        while (page <= maxPositionedPage || autoQueue.isNotEmpty()) {
            val grid = mutableMapOf<Int, Quest>()

            for (quest in positionedByPage[page].orEmpty()) {
                val (_, localRow, localColumn) = validatedPositions.getValue(quest.id)
                grid[MenuUtils.rowColumnToSlot(localRow, localColumn, columnSize)] = quest
            }

            for (slotIndex in 0 until pageSize) {
                if (slotIndex !in grid && autoQueue.isNotEmpty()) {
                    grid[slotIndex] = autoQueue.removeFirst()
                }
            }

            pages.add(grid)
            page++
        }

        if (pages.isEmpty()) {
            pages.add(emptyMap())
        }

        return pages
    }

    private fun pagesFor(player: Player): List<Map<Int, Quest>> {
        return cache.getOrPut(player.uniqueId) { buildPages(player) }
    }

    fun invalidate(player: Player) {
        cache.remove(player.uniqueId)
    }

    override fun getQuestAt(
        player: Player,
        page: Int,
        row: Int,
        column: Int,
        columnSize: Int,
        pageSize: Int
    ): Quest? {
        val grid = pagesFor(player).getOrNull(page - 1) ?: return null
        return grid[MenuUtils.rowColumnToSlot(row, column, this.columnSize)]
    }

    override fun getPages(player: Player, pageSize: Int): Int {
        return pagesFor(player).size
    }
}
