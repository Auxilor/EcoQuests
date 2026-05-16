# EcoQuests — Click-to-Start

## Goal

Let players left-click a quest icon in the `/quests` GUI to start the quest, instead of having to run `/ecoquests start <player> <quest>`. This is especially valuable for Bedrock / console players who can't easily run commands.

The feature must:

- Be additive: the existing `/ecoquests start` admin command keeps its current behaviour and signature.
- Be globally toggleable via `gui.click-to-start` in `config.yml` (default `true`).
- Surface a configurable "Click to start!" lore hint on icons that are currently startable for the viewing player.
- Funnel into the existing `Quest.start(player)` method so that `start-effects`, the `PlayerQuestStartEvent`, and the existing `quests.start` announcement (handled by `QuestStartDisplay`) all keep working unchanged.
- Send player-flavoured chat feedback for both success and the various failure states, using `lang.yml`.
- Refresh the GUI in place after a successful start so the icon's lore reflects the new state. Do not close the menu.

## Files Touched

| Path | Change |
|---|---|
| `eco-core/core-plugin/src/main/kotlin/com/willfp/ecoquests/quests/Quest.kt` | Add `isStartableBy`, add `tryStartFromGui`, append click-to-start lore inside the `slot` item builder, add `onLeftClick` handler to the slot. |
| `eco-core/core-plugin/src/main/resources/config.yml` | Add `gui.click-to-start` flag and `quests.icon.click-to-start-lore` list. |
| `eco-core/core-plugin/src/main/resources/lang.yml` | Add `started-quest-self`, `cannot-start-conditions`, `cannot-start-completed`. (`already-started` already exists and is reused.) |

No other files change. **Do not touch** `CommandStart.kt`, `QuestsGUI.kt`, `QuestAreaComponent.kt`, `Quests.kt`, `QuestStartDisplay.kt`, or any GUI component file.

## Existing Code Context

### `Quest.slot` (current — `Quest.kt:52-74`)

```kotlin
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

        setDisplayName(
            addPlaceholdersInto(
                listOf(plugin.configYml.getString("quests.icon.name")),
                player
            ).first()
        )
    }
}) {

}
```

The trailing `{ }` is the slot-builder lambda where click handlers live. The first lambda is the per-player item provider. Click handlers in this codebase use the `onLeftClick` extension from `com.willfp.eco.core.gui.onLeftClick` — see existing usage in:

- `eco-core/core-plugin/src/main/kotlin/com/willfp/ecoquests/gui/components/QuestInfoComponent.kt:22` — `onLeftClick { player, _, _, _ -> ... }`
- `eco-core/core-plugin/src/main/kotlin/com/willfp/ecoquests/gui/components/CloseButton.kt:16` — `onLeftClick { event, _ -> event.whoClicked.closeInventory() }`
- `eco-core/core-plugin/src/main/kotlin/com/willfp/ecoquests/gui/components/BackButton.kt:15` — `onLeftClick { player, _, _, _ -> ... }`

The first parameter is sometimes typed as `InventoryClickEvent` (CloseButton style) and sometimes as `Player` (QuestInfoComponent / BackButton style). Both forms are valid eco overloads. **Use the four-parameter form** to match the in-package convention:

```kotlin
onLeftClick { player, _, _, menu ->
    tryStartFromGui(player, menu)
}
```

If the four-arg lambda's `menu` parameter does not actually expose a `Menu` (verify in IDE), fall back to the two-arg form `onLeftClick { event, menu -> tryStartFromGui(event.whoClicked as Player, menu) }`. Either way, the goal is to obtain the `Menu` reference so it can be refreshed.

### Existing `Quest.start` (`Quest.kt:314-329`) — must be reused, do not reimplement

```kotlin
fun start(player: Player) {
    if (hasStarted(player)) {
        return
    }

    startEffects?.trigger(player.toDispatcher())
    player.profile.write(hasStartedKey, true)
    player.profile.write(startedTimeKey, currentTimeMinutes)

    // Reset tasks to generate new xp requirements
    for (task in tasks) {
        task.reset(player)
    }

    Bukkit.getPluginManager().callEvent(PlayerQuestStartEvent(player, this))
}
```

The existing `meetsStartConditions(player)` method (`Quest.kt:293`), `hasStarted(player)` (`Quest.kt:301`), and `hasCompleted(player)` (`Quest.kt:289`) are the three predicates needed for the click-to-start guards. Use them.

### Existing `addPlaceholdersInto` (`Quest.kt:454`)

Already used inside the `slot` item builder for the main lore. Reuse it for the click-to-start hint so the hint supports `%quest%` and friends.

### `plugin` reference

`Quest` already holds `private val plugin: EcoPlugin` (constructor parameter, `Quest.kt:40`) — use `plugin.configYml.getBool(...)`, `plugin.configYml.getStrings(...)`, `plugin.langYml.getMessage(...)` directly. Do not import the top-level `com.willfp.ecoquests.plugin` global.

## Implementation

### 1. `lang.yml` additions

In `eco-core/core-plugin/src/main/resources/lang.yml`, under the `messages:` block, alongside the existing `started-quest`, add:

```yaml
  started-quest-self: "&aYou started the &f%quest% &aquest!"
  cannot-start-conditions: "&cYou don't meet the requirements to start this quest!"
  cannot-start-completed: "&cYou've already completed this quest!"
```

The existing `already-started: "&cThe player has already started this quest!"` message is reused as-is for the in-progress case (it reads naturally as player-facing too — "the player" reads fine in self-context, and changing it would break the admin command's wording).

### 2. `config.yml` additions

In `eco-core/core-plugin/src/main/resources/config.yml`:

**A.** Inside the top-level `gui:` block (the one starting at line 13, *not* the `completed-gui:` block) add a new key. Place it directly under `gui:` at the same indent level as `title:` / `rows:` / `mask:` (two-space indent). Suggested location: immediately after the `rows: 6` line.

```yaml
  # If true, players can left-click a quest icon in this GUI to start that quest.
  click-to-start: true
```

**B.** Inside the `quests.icon:` block (starting at line 147) add a new key alongside `name`, `line-wrap`, and `lore`. Place it immediately after the `lore:` list (after line 159, i.e. after the `- "%time_since%"` line):

```yaml
    # Lore lines appended to the icon when the quest is currently startable
    # (not started, not completed, start-conditions met, click-to-start enabled).
    # Set to [] to disable the hint.
    click-to-start-lore:
      - ""
      - "&eClick to start!"
```

Indent levels: `click-to-start-lore:` sits at 4 spaces (same as `lore:`); each list item sits at 6 spaces (same as the existing `lore` entries).

### 3. `Quest.kt` changes

All three logical changes are inside this one file.

#### 3a. New helper method

Add as a public method on `Quest`. Placement: directly above the existing `fun start(player: Player)` at `Quest.kt:314`.

```kotlin
fun isStartableBy(player: Player): Boolean {
    return !hasCompleted(player) && !hasStarted(player) && meetsStartConditions(player)
}
```

#### 3b. New `tryStartFromGui` method

Add directly below `isStartableBy`, still above `fun start`. This is the single entry point used by the click handler.

```kotlin
fun tryStartFromGui(player: Player, menu: com.willfp.eco.core.gui.menu.Menu) {
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

    start(player)

    player.sendMessage(
        plugin.langYml.getMessage("started-quest-self")
            .replace("%quest%", name)
    )

    menu.refresh(player)
}
```

Add the import at the top of the file:

```kotlin
import com.willfp.eco.core.gui.menu.Menu
```

…and change the parameter type to `Menu` (drop the fully-qualified inline form). The fully-qualified form above is shown only so it's unambiguous which `Menu` is meant — eco's GUI menu, not Bukkit's.

If `Menu#refresh(Player)` is not the exact API name in this version of eco (verify against the imported `Menu` class in the IDE), use whatever the eco menu's per-player re-render method is — equivalent calls are typically `render`, `refresh`, or scheduling a one-tick re-open. As a last-resort fallback, schedule `plugin.scheduler.runTask(player) { menu.open(player) }` to re-open the same menu (preserves page state because eco persists per-player menu page).

#### 3c. Append click-to-start lore inside the `slot` item builder

Modify the existing `slot({ player, _ -> ... }) { }` block (`Quest.kt:52-74`) so the item builder appends the hint lines when applicable. Place the new `addLoreLines` call **after** the existing `startConditions.getNotMetLines(...)` block and **before** `setDisplayName(...)`:

```kotlin
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
```

Add the import at the top of the file:

```kotlin
import com.willfp.eco.core.gui.onLeftClick
```

(Same import string already used in `QuestInfoComponent.kt:4` and `BackButton.kt:4`.)

The `player` lambda parameter in `onLeftClick { player, _, _, menu -> ... }` is already `org.bukkit.entity.Player` — `Player` is already imported at `Quest.kt:34` so no further import is required.

## Behaviour Summary

| Player state when icon clicked | Outcome |
|---|---|
| Global `gui.click-to-start: false` | Silent no-op. Hint never shown. |
| Quest already completed | `cannot-start-completed` chat message. |
| Quest already in progress | `already-started` chat message. |
| Start conditions not met | `cannot-start-conditions` chat message. (Hint also not shown on icon.) |
| Startable | `Quest.start(player)` runs (firing `start-effects` and `PlayerQuestStartEvent`, which `QuestStartDisplay` consumes for the configured `quests.start` announcement). `started-quest-self` chat message sent. Menu refreshed in place — icon now shows "Started 0m ago" via the existing `%time_since%` placeholder, and the click-to-start hint disappears because `isStartableBy` now returns false. |

## Out of Scope

- Per-quest override of click-to-start (e.g., a `gui.click-to-start` key inside individual quest YAMLs). The global flag is the only knob.
- Right-click / shift-click handlers. Only `onLeftClick`.
- Closing the GUI on start.
- Sound effects on click — admins can already configure these per-quest via `start-effects`.
- Changing the existing `started-quest` admin lang message or the `CommandStart` command's behaviour.
- Click-to-start in `PreviousQuestsGUI` (the completed-quests history view) — completed quests aren't startable anyway.
