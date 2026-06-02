---
title: "How to Make a Task"
sidebar_position: 2
---

A **task** is a single goal a player works toward inside a quest. It gains **XP** from in-game **triggers**, and when its XP requirement is met it can run **effects**. Each task is its own config file, and this page walks you through building one.

## Quick start

1. Open the `/tasks/` folder and copy `_example.yml` to a new file, e.g. `mine_stone.yml`. The file name is the task ID.
2. Set the `description`, using `%xp%` and `%required-xp%` to show progress.
3. Add one or more `xp-gain-methods`, each with a `trigger` and a `multiplier` or `value`.
4. Reference the task ID from a quest's `tasks` list, then run `/ecoquests reload`.
5. Start the quest with `/quests`, perform the action, and confirm the task XP goes up.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real task. You can also organise tasks into subfolders inside `tasks/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the task ID; it's what quests and placeholders use to reference the task. Items referenced anywhere in the file use the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the task will not load.
:::

## The structure of a task

| Part | What it controls |
| --- | --- |
| **Description** | The text shown for the task in GUIs and messages |
| **XP gain methods** | Which triggers grant XP, and how much |
| **On-complete effects** | What runs when the task is completed |

```yaml
# === Description: the text shown for the task ===
description: "&fBreak stone blocks (&a%xp%&8/&a%required-xp%&f)" # %xp% and %required-xp% show progress

# === XP gain methods: which triggers grant XP ===
xp-gain-methods:
  - trigger: mine_block # The action that grants XP
    multiplier: 0.5 # Scales the value the trigger produces; use 'value' for a flat count instead
    args:
      chance: 50 # Trigger-specific args
    filters: # Only count XP when these filters match
      blocks:
        - netherrack

# === On-complete effects: what runs when the task is done ===
on-complete: # Effects run when the player completes the task
  - id: send_message
    args:
      message: "Task Completed!"
```

### Description

The line shown for the task wherever it appears.

```yaml
description: "&fBreak stone blocks (&a%xp%&8/&a%required-xp%&f)" # %xp% and %required-xp% show live progress
```

### XP gain methods

How the task earns XP from in-game actions. Each method takes a trigger, a multiplier or value, and optional conditions and filters.

```yaml
xp-gain-methods:
  - trigger: move # The action that grants XP
    multiplier: 0.5 # Scales the trigger's value; use 'value' to count a flat number instead
    conditions: # Only grant XP when these conditions are met
      - id: in_world
        args:
          world: world_nether
```

### On-complete effects

The effects that run the moment the task is completed.

```yaml
on-complete: # Optional; effects run when a player completes the task
  - id: send_message
    args:
      message: "Task Completed!"
```

:::danger Effects are their own system
`on-complete` runs on the effect and condition system, which is shared across every eco plugin and documented separately.

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect)
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain)
:::

## Internal placeholders

| Placeholder | Value |
| --- | --- |
| `%xp%` | The XP the player has for the task |
| `%required-xp%` | The XP required to complete the task |

:::tip Troubleshooting
- **Task not loading?** Check the file name is lowercase letters, numbers, and underscores only, and that it isn't prefixed with `_`.
- **XP never increases?** Make sure the `trigger` is correct and that your `filters` and `conditions` actually match the action you're performing.
- **Task missing from a quest?** Confirm the quest's `tasks` list references the task ID exactly, then reload.
:::

<hr/>

## Where to go next

- **Wire it into a quest:** [How to make a quest](how-to-make-a-quest) shows how quests reference tasks.
- **Default examples:** the shipped task configs are [here](https://github.com/Auxilor/EcoQuests/tree/master/eco-core/core-plugin/src/main/resources/tasks).
- **Community configs:** browse and import more on [lrcdb](https://lrcdb.auxilor.io/).