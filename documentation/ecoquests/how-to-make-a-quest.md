---
title: "How to Make a Quest"
sidebar_position: 1
---

A **quest** is a goal made up of one or more **tasks**; when a player finishes every selected task, the quest completes and runs its **rewards**. Each quest is its own config file, and this page walks you through building one from scratch.

## Quick start

1. Open the `/quests/` folder and copy `_example.yml` to a new file, e.g. `traveller.yml`. The file name is the quest ID.
2. Set the `name`, `description`, and `reset-time` for your quest.
3. Add one or more entries under `tasks`, each referencing a task ID and its `xp` requirement.
4. Configure the `rewards` to run when the quest completes.
5. Run `/ecoquests reload`, then open `/quests` and confirm your quest shows up in the book.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real quest. You can also organise quests into subfolders inside `quests/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the quest ID; it's what you use in commands and placeholders. Items referenced anywhere in the file (e.g. the GUI icon) use the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the quest will not load.
:::

## The structure of a quest

| Part | What it controls |
| --- | --- |
| **Quest info** | The name, description, and reset timer |
| **Tasks** | The goals players complete and their XP requirements |
| **Rewards** | What the player gets when the quest completes |
| **Quest start** | When and how the quest begins |
| **GUI** | How the quest appears in the `/quests` menu |

```yaml
# === Quest info: name, description, reset ===
name: "Traveller" # Shown in the GUI and the %quest% placeholder
description: "&7Stretch your legs! Walk around The Nether and find new places to explore."
reset-time: -1 # Minutes between resets; -1 disables. 1 day: 1440, 1 week: 10080, 1 month: 43200

# === Tasks: the goals and their XP requirements ===
tasks:
  - task: move # A task ID from the /tasks/ folder
    xp: 1000 # XP needed on this task; use 1 for a single-action task
task-amount: -1 # How many of the above tasks to pick (resettable quests); -1 uses all

# === Rewards: what the player gets on completion ===
reward-messages: # Lines shown by the %rewards% placeholder in icons and messages
  - " &8» &r&f+2 %ecoskills_defense_name%"
rewards: # Effects run when the quest completes
  - id: give_item
    args:
      items:
        - emerald 5

# === Quest start: when and how the quest begins ===
announce-start: false # Tell the player when the quest auto-starts
start-effects: [] # Effects run when the quest starts
start-conditions: # Conditions that auto-start the quest when met
  - id: in_world
    args:
      world: world_nether
auto-start: true # Start automatically when conditions are met; if false, only /ecoquests start works

# === GUI: how the quest appears in /quests ===
gui:
  enabled: true # Show this quest in the GUI
  always: false # Show even when not started
  item: paper # GUI icon, read via the Item Lookup System
```

### Quest info

The basic identity of the quest and how often it resets.

```yaml
name: "Traveller" # Shown in the GUI and the %quest% placeholder
description: "&7Stretch your legs! Walk around The Nether and find new places to explore."
reset-time: -1 # Minutes between resets; -1 disables. 1 day: 1440, 1 week: 10080, 1 month: 43200
```

### Tasks

The list of tasks the player must complete, each with the XP needed to finish it.

```yaml
# XP requirements accept placeholder math, e.g. %ecoskills_combat% * 100, or random(min,max)
tasks:
  - task: move # A task ID from the /tasks/ folder
    xp: 1000 # Use 1 when the task is a single action

# For resettable quests, how many of the above tasks to select; -1 uses all
task-amount: -1
```

### Rewards

What the player receives when every task is done.

```yaml
reward-messages: # Lines shown by the %rewards% placeholder in icons and messages
  - " &8» &r&f+2 %ecoskills_defense_name%"
rewards: # Effects run when the quest completes
  - id: give_item
    args:
      items:
        - emerald 5
```

:::danger Effects are their own system
`rewards`, `start-effects`, and `start-conditions` all run on the effect and condition system, which is shared across every eco plugin and documented separately.

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect)
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain)
:::

### Quest start

Controls when the quest begins and what happens at that moment.

```yaml
announce-start: false # Tell the player when the quest auto-starts
start-effects: [] # Effects run the moment the quest starts
start-conditions: # When all are met, the quest auto-starts (if auto-start is true)
  - id: in_world
    args:
      world: world_nether
# If gui.always is true, unmet start-conditions show as not-met lines on the GUI icon
auto-start: true # If false, the quest can only be started with /ecoquests start
```

### GUI

How the quest is displayed in the `/quests` book.

```yaml
gui:
  enabled: true # Show this quest in the GUI
  always: false # Show even when the player hasn't started it
  item: paper # GUI icon, read via the Item Lookup System
```

:::tip Troubleshooting
- **Quest not loading?** Check the file name is lowercase letters, numbers, and underscores only, and that it isn't prefixed with `_`.
- **Quest missing from the GUI?** Set `gui.enabled` to true, and `gui.always` to true if you want it visible before it's started.
- **Quest never auto-starts?** Confirm `auto-start` is true and the player actually meets every entry under `start-conditions`.
:::

<hr/>

## Where to go next

- **Build the goals:** [How to make a task](how-to-make-a-task) covers the tasks a quest references.
- **Default examples:** the shipped quest configs are [here](https://github.com/Auxilor/EcoQuests/tree/master/eco-core/core-plugin/src/main/resources/quests).
- **Community configs:** browse and import more on [lrcdb](https://lrcdb.auxilor.io/).