---
title: How to make a Quest
sidebar_position: 1
---

## How to add quests
Each quest is its own config file, placed in the `/quests/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the Quest is the file name. This is what you use in commands and placeholders.
ID's must be lowercase letters, numbers, and underscores only.

Quests are made up of specific [tasks](https://plugins.auxilor.io/ecoquests/how-to-make-a-task), and when all tasks are completed the quest will complete, giving the player rewards!

## Example Quest Config

```yaml
name: "Traveller"
description: "&7Stretch your legs! Walk around The Nether and find new places to explore."
reset-time: -1

tasks:
  - task: move
    xp: 1000
task-amount: -1

reward-messages:
  - " &8» &r&f+2 %ecoskills_defense_name%"
rewards: []

announce-start: false
start-effects: []
start-conditions:
  - id: in_world
    args:
      world: world_nether
auto-start: true

gui:
  enabled: true
  always: false
  item: paper
```

## Understanding all the sections

### The Quest Info Section
```yaml
name: "Traveller" # The name of the task
description: "&7Stretch your legs! Walk around The Nether and find new places to explore."

# How many minutes between this quest being reset (set to -1 to disable)
# 1 Day: 1440
# 1 Week: 10080
# 1 Month: 43200
reset-time: -1
```

### The Tasks Section
```yaml
# A list of tasks and their XP requirements to complete this quest.
# If the task is one action, set XP to 1.
# XP requirements can use placeholder math, for example %ecoskills_combat% * 100, or random values, like random(min,max)
tasks:
  - task: move
    xp: 1000

# (For resettable tasks) The amount of tasks to select from the list above.
# Set to -1 to use all tasks.
task-amount: -1
```

### The Rewards Section
:::danger Effects Section

The rewards section uses the effects system. You can configure effects, conditions, filters, and mutators in this section to run when the quest is completed.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::
```yaml
# The messages for the %rewards% placeholder in icons, messages, etc.
reward-messages:
  - " &8» &r&f+2 %ecoskills_defense_name%"

# A list of effects to run when the quest is completed.
# Read https://plugins.auxilor.io/effects/configuring-an-effect
rewards:
  - id: give_item
    args:
      items:
        - emerald 5
```

### The Quest Start Section
:::danger Effects Section

The start section uses the effects system. You can configure effects, conditions, filters, and mutators in this section to run when the quest is started.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::
```yaml
# If the player should be told when they have started the quest.
announce-start: false

# A list of effects to run when the quest is started.
# Read https://plugins.auxilor.io/effects/configuring-an-effect
start-effects: []

# A list of conditions required to start the quest.
# The quest will be automatically started when these conditions are met.
# Read https://plugins.auxilor.io/conditions/configuring-a-condition
# If gui.always is true, then not-met-lines will show up on the GUI icon!
start-conditions: []

# If the quest should auto start when all conditions are met
# If this is set to false, the quest can only be started with /ecoquests start
auto-start: true
```

### The GUI Section
```yaml
# Options for the /quests GUI
gui:
  enabled: true # If the quest should be shown in the GUI
  always: false # If the quest should always be in the GUI, even if it's not started
  # The item to show in the GUI, read https://plugins.auxilor.io/the-item-lookup-system
  item: paper
```

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoQuests/tree/master/eco-core/core-plugin/src/main/resources/quests). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).