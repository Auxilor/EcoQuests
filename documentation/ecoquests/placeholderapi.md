---
title: "PlaceholderAPI"
sidebar_position: 4
---

| Placeholder                                         | Description                                                 |
|-----------------------------------------------------| ----------------------------------------------------------- |
| `%ecoquests_quests_amount%`                         | The total amount of quests on the server                    |
| `%ecoquests_quests_completed%`                      | The amount of quests the player has completed               |
| `%ecoquests_quests_active%`                         | The amount of quests the player has active                  |
| `%ecoquests_quests_percent_completed%`              | The percentage of quests the player has completed           |
| `%ecoquests_recent_quest_name%`                     | The name of the quest the player has most recently started  |
| `%ecoquests_quest_<quest>_name%`                    | The quest name                                              |
| `%ecoquests_quest_<quest>_description%`             | The quest description                                       |
| `%ecoquests_quest_<quest>_tasks%`                   | The amount of tasks in the quest                            |
| `%ecoquests_quest_<quest>_tasks_completed%`         | The amount of tasks the player has completed in the quest   |
| `%ecoquests_quest_<quest>_started%`                 | If the player has started the quest (true / false)          |
| `%ecoquests_quest_<quest>_completed%`               | If the player has completed the quest (true / false)        |
| `%ecoquests_quest_<quest>_time_until_reset%`        | The amount of time until the quest resets                   |
| `%ecoquests_quest_<quest>_time_since_start%`        | The amount of time since the player has started the quest   |
| `%ecoquests_quest_<quest>_time_since_completed%`    | The amount of time since the player has completed the quest |
| `%ecoquests_quest_<quest>_time_since%`              | Time since start / completion / "Not Yet Started"           |
| `%ecoquests_quest_<quest>_task_<task>_required_xp%` | The XP required to complete the task                        |
| `%ecoquests_quest_<quest>_task_<task>_xp%`          | The XP the player has for the task                          |
| `%ecoquests_quest_<quest>_task_<task>_description%` | The description of the task                                 |
| `%ecoquests_quest_<quest>_task_<task>_completed%`   | If the player has completed the task (true / false)         |

### Task-Amount Placeholders
These placeholders are dependent on the amount of tasks in the quest, they return the info on the active tasks.

Example: `task-amount: 1` You would use `%ecoquests_quest_<quest>_task_1_required_xp%` to see XP required for the 1st task.

| Placeholder                                                               | Description                                                           |
|---------------------------------------------------------------------------|-----------------------------------------------------------------------|
| `%ecoquests_quest_<quest>_task_<task_number[0-9]>_required_xp%`           | The XP required to complete the [numbered] active task                |
| `%ecoquests_quest_<quest>_task_<task_number[0-9]>_xp%`                    | The XP the player has for the [numbered] active task                  |
| `%ecoquests_quest_<quest>_task_<task_number[0-9]>_completed%`             | If the player has completed the [numbered] active task (true / false) |
| `%ecoquests_quest_<quest>_task_<task_number[0-9]>_description%`           | The description of the [numbered] active task                         |
| `%ecoquests_quest_<quest>_task_<task_number[0-9]>_completed_description%` | The description (including completion) of the [numbered] active task  |