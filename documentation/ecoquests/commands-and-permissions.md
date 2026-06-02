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

<hr/>

## Where to go next

- **Make a quest:** [How to make a quest](how-to-make-a-quest) covers building quests you can start and reset.
- **Placeholders:** [PlaceholderAPI](placeholderapi) lists the placeholders for use in GUIs and messages.

