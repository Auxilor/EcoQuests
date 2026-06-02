---
title: "Plugin Config"
sidebar_position: 6
---

This is the main `config.yml` for EcoQuests, found at `/plugins/EcoQuests/config.yml`. It controls global behaviour: the quest book GUI, the completed-quests GUI, and the messages, titles, and sounds shown when a quest starts or completes. Edit it, then run `/ecoquests reload` to apply your changes.

:::warning
Changing `use-local-storage` switches where player data is stored. Restart the server after changing it; a reload alone will not migrate or refresh data.
:::

## Default config.yml

```yaml
# Even if eco is set up to use a database, you can force EcoQuests
# to save to local storage to disable cross-server sync.
use-local-storage: false

scan-interval: 20 # How often to scan for quests auto-starting (in ticks)

gui:
  title: "Quest Book" # Title of the /quests GUI

  rows: 6 # Number of rows in the GUI

  # If true, players can left-click a quest icon in this GUI to start that quest.
  click-to-start: true

  # The mask fills empty GUI slots using a list of materials and a pattern.
  mask:
    # Each pattern line is one GUI row and must be 9 long; the row count
    # must match rows above. 0 is empty, 1 is the first material, 2 the
    # second, and so on, up to 9.
    materials: # Materials referenced by number in the pattern
      - gray_stained_glass_pane
      - black_stained_glass_pane
    pattern: # One string per row, 9 characters each
      - "111101111"
      - "100000001"
      - "100000001"
      - "100000001"
      - "100000001"
      - "111111111"

  quest-info: # The book icon summarising the player's quest progress
    item: writable_book # Icon material
    name: "&fQuest Book" # Icon display name
    lore: # Icon lore; supports placeholders
      - ""
      - "&7Quests Completed: &f%ecoquests_quests_completed%"
      - "&7Quests Active: &f%ecoquests_quests_active%"
      - ""
      - "&eClick to view past quests!"

    location: # Slot for this icon (1-indexed)
      row: 1
      column: 5

  quest-area: # Rectangle of slots that quest icons fill
    top-left:
      row: 2
      column: 2
    bottom-right:
      row: 5
      column: 8

  prev-page: # Previous-page button
    item: arrow name:"&fPrevious Page"
    location:
      row: 6
      column: 4

  next-page: # Next-page button
    item: arrow name:"&fNext Page"
    location:
      row: 6
      column: 6

  close: # Close button
    item: barrier
    name: "&cClose"
    location:
      row: 6
      column: 5

  # Custom GUI slots; see here for a how-to: https://plugins.auxilor.io/all-plugins/custom-gui-slots
  custom-slots: [ ]

completed-gui: # The GUI listing quests the player has finished
  title: "Completed Quests" # Title of the completed-quests GUI

  rows: 6 # Number of rows in the GUI

  # Fills empty slots; same materials-and-pattern rules as above.
  mask:
    materials: # Materials referenced by number in the pattern
      - gray_stained_glass_pane
      - black_stained_glass_pane
    pattern: # One string per row, 9 characters each
      - "111111111"
      - "100000001"
      - "100000001"
      - "100000001"
      - "100000001"
      - "011111111"

  quest-area: # Rectangle of slots that completed-quest icons fill
    top-left:
      row: 2
      column: 2
    bottom-right:
      row: 5
      column: 8

  prev-page: # Previous-page button
    item: arrow name:"&fPrevious Page"
    location:
      row: 6
      column: 4

  next-page: # Next-page button
    item: arrow name:"&fNext Page"
    location:
      row: 6
      column: 6

  back: # Button returning to the main quest book
    item: arrow name:"&fBack"
    location:
      row: 6
      column: 1

  # Custom GUI slots; see here for a how-to: https://plugins.auxilor.io/all-plugins/custom-gui-slots
  custom-slots: [ ]

tasks: # How a task line renders inside a quest icon
  completed: "&a&l✔ &r&f%description%" # Line shown when a task is done
  not-completed: "&c&l❌ &r&f%description%" # Line shown when a task is not done

quests:
  icon: # How each quest renders in the GUI
    name: "&e%quest%" # The name of the icon
    line-wrap: 32 # Wrap lore lines at this character width
    lore: # Icon lore; supports quest placeholders
      - "%description%"
      - ""
      - "&fTasks:"
      - " %tasks%"
      - ""
      - "&fRewards:"
      - " %rewards%"
      - ""
      - "%time_since%"

    # Lore lines appended to the icon when the quest is currently startable
    # (not started, not completed, start-conditions met, click-to-start enabled).
    # Set to [] to disable the hint.
    click-to-start-lore:
      - ""
      - "&eClick to start!"

  complete: # What the player sees and hears when a quest completes
    message:
      enabled: true # Send the chat message
      message:
        - "&f"
        - " &#eacda3&lQUEST COMPLETE: &f%quest%"
        - "&f"
        - " &#eacda3&lREWARDS:"
        - "%rewards%"
        - "&f"
    title:
      enabled: true # Show the on-screen title
      fade-in: 0.5 # Seconds
      stay: 4 # Seconds
      fade-out: 0.5 # Seconds
      title: "&#eacda3&lQuest Complete!"
      subtitle: "&f%quest%"
    sound:
      enabled: true # Play the sound
      sound: ui_toast_challenge_complete # Sound to play
      pitch: 1.5 # Between 0.5 and 2
      volume: 1.0 # Volume
      category: player # Sound category

  start: # What the player sees and hears when a quest starts
    message:
      enabled: true # Send the chat message
      message:
        - "&f"
        - " &#eacda3&lNEW QUEST: &f%quest%"
        - "&f"
        - " &#eacda3&lREWARDS:"
        - "%rewards%"
        - "&f"
    title:
      enabled: false # Show the on-screen title
      fade-in: 0.5 # Seconds
      stay: 4 # Seconds
      fade-out: 0.5 # Seconds
      title: "&#eacda3&lNew Quest!"
      subtitle: "&f%quest%"
    sound:
      enabled: true # Play the sound
      sound: entity_player_levelup # Sound to play
      pitch: 1.9 # Between 0.5 and 2
      volume: 1.0 # Volume
      category: player # Sound category
```

<hr/>

## Where to go next

- **Make a quest:** [How to make a quest](how-to-make-a-quest) covers the per-quest config files.
- **GUI item references:** the `item` fields use the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).
- **Custom GUI slots:** read the [custom GUI slots how-to](https://plugins.auxilor.io/all-plugins/custom-gui-slots).

