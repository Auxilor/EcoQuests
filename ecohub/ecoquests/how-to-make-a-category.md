---
title: "How to Make a Category"
sidebar_position: 3
---

A **category** groups quests together so players can track progress across a set of related goals. When a player completes every quest in a category, the category fires its own rewards and triggers a libreforge event.

## Quick start

1. Open the `/categories/` folder and create a new file, e.g. `easy.yml`. The file name is the category ID.
2. Set the `name` and optionally configure `effects` to run on completion.
3. Add `category: <id>` to any quest you want grouped under this category.
4. Run `/ecoquests reload`.

:::tip
`_example.yml` is included as a reference and is **never loaded**. You can organise category files into subfolders and they will still load.
:::

## Category file structure

```yaml
# The ID of the category is the name of the .yml file.
# _example.yml is not loaded.

name: "Easy"

# Effects run when the player completes this category (all quests in it done).
# This fires every time the category is completed, including after a reset cycle.
# Read https://plugins.auxilor.io/effects/configuring-an-effect
effects: []
```

## Assigning a quest to a category

Add one optional line to any quest config:

```yaml
name: "Traveller"
category: easy   # The category ID (matches the file name without .yml)
# ... rest of quest config
```

Omit the line entirely for quests that belong to no category. If the category ID does not match a loaded category file, a warning is printed to the console and the quest behaves normally.

:::warning ID rules
Category IDs follow the same rules as quest IDs: lowercase letters, numbers, and underscores only (a-z, 0-9, _).
:::

## Placeholders

Swap `<category>` for the category ID.

| Placeholder | Description |
| --- | --- |
| `%ecoquests_category_<category>_name%` | The display name of the category |
| `%ecoquests_category_<category>_complete%` | Whether the player has completed all quests in the category (true / false) |
| `%ecoquests_category_<category>_quests_completed%` | How many quests in the category the player has completed |
| `%ecoquests_category_<category>_quests_remaining%` | How many quests in the category the player has not yet completed |

`_complete` is computed live on every call, so it reflects the current state after any quest resets.

## Libreforge integration

Categories expose a trigger, a condition, and a filter for use anywhere libreforge effects are supported.

### Trigger: `complete_category`

Fires when a player completes a category (all quests in the category become completed).

```yaml
triggers:
  - id: complete_category
```

Fires every time the category completes, so if a resettable quest resets and the player completes it again, the trigger fires again.

### Condition: `has_completed_category`

Passes when the player has completed all quests in the given category.

```yaml
conditions:
  - id: has_completed_category
    args:
      category: easy
```

### Filter: `category`

Filters any quest or task trigger event by the category of the quest involved. Passes automatically when the triggering event is not a quest event.

```yaml
filters:
  - id: category
    args:
      category:
        - easy
        - medium
```

Use this to run effects only when a player completes (or starts) a quest belonging to a specific category, without listing every quest individually.

<hr/>

## Where to go next

- **Build quests for your category:** [How to make a quest](how-to-make-a-quest) covers the `category` field and the full quest config.
- **Track progress:** [PlaceholderAPI](placeholderapi) lists all category placeholders.
