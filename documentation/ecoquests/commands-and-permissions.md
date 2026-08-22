---
title: "Commands and Permissions"
sidebar_position: 5
---

Every EcoQuests command and the permission node that gates it. Player commands default to enabled; admin `/ecoquests` commands are op-only.

| Command                                              | Description                                            | Permission                      |
|------------------------------------------------------|--------------------------------------------------------|---------------------------------|
| `/quests, /quest, /q`                                | Open the main menu                                     | `ecoquests.command.quests`      |
| `/quests cancel <quest>`                             | Cancel an active quest, resetting all progress         | `ecoquests.command.quests.cancel` |
| `/ecoquests reset <quest>`                           | Reset a quest                                          | `ecoquests.command.reset`       |
| `/ecoquests resetplayer <player> <quest>`            | Reset a quest for a player                             | `ecoquests.command.resetplayer` |
| `/ecoquests start <player> <quest>`                  | Start a quest for a player                             | `ecoquests.command.start`       |
| `/ecoquests addexp <player> <quest> <task> <amount>` | Add quest experience for a player                      | `ecoquests.command.addexp`      |
| `/ecoquests import <id>`                             | Import a quest from [lrcdb](https://lrcdb.auxilor.io/) | `ecoquests.command.import`      |
| `/ecoquests export <id>`                             | Export a quest to [lrcdb](https://lrcdb.auxilor.io/)   | `ecoquests.command.export`      |

### Additional Permissions

| Permission                    | Description                                                                                                     |
|--------------------------------|------------------------------------------------------------------------------------------------------------------|
| `ecoquests.quests.max.<number>` | Limit the amount of quests a player can have active at once, overriding the `max-active-quests` config option. If a player holds multiple, the highest wins |

<hr/>

## Where to go next

- **Make a quest:** [How to make a quest](how-to-make-a-quest) covers building quests you can start and reset.
- **Plugin config:** [Plugin Config](plugin-config) covers the `max-active-quests` default and other global settings.
- **Placeholders:** [PlaceholderAPI](placeholderapi) lists the placeholders for use in GUIs and messages.

