---
title: How to make a Task
sidebar_position: 2
---

## How to add tasks
Each task is its own config file, placed in the `/tasks/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the Task is the file name. This is what you use in quests and placeholders.
ID's must be lowercase letters, numbers, and underscores only.

Tasks are the goals that players must complete in order to complete [quests](https://plugins.auxilor.io/ecoquests/how-to-make-a-quest).

## Example Task Config

```yaml
description: "&fBreak stone blocks (&a%xp%&8/&a%required-xp%&f)"

xp-gain-methods:
  - trigger: mine_block
    multiplier: 0.5
    args:
      chance: 50
    filters:
      blocks:
        - netherrack

on-complete:
  - id: send_message
    args:
      message: "Task Completed!"
```

## Understanding all the sections

### The Task Config Section
```yaml
description: "&fBreak stone blocks (&a%xp%&8/&a%required-xp%&f)" # The description of the task.
```

### The XP Gain Methods Section
```yaml
# An XP gain method takes a trigger, a multiplier, conditions, and filters.
# The 'multiplier' takes the value produced by the trigger and multiplies it
# Alternatively, you can use 'value' to count a specific number and not a multiplier
xp-gain-methods:
  - trigger: move
    multiplier: 0.5
    conditions:
      - id: in_world
        args:
          world: world_nether
```

### The On-Complete Effects Section
:::danger Effects Section

The effects section is the core functionality of the task. You can configure effects, conditions, filters, and mutators in this section to run when the task is completed.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::
```yaml
# An optional list of effects to run when a player completes the task
# Read here: https://plugins.auxilor.io/effects/configuring-an-effect
on-complete:
  - id: send_message
    args:
      message: "Task Completed!"
```

## Internal Placeholders

| Placeholder     | Value                                          |
| --------------- | ---------------------------------------------- |
| `%xp%`          | The amount of XP the player has for the task   |
| `%required-xp%` | The amount of XP required to complete the task |

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoQuests/tree/master/eco-core/core-plugin/src/main/resources/tasks). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).
