---
title: "API"
sidebar_position: 7
---

This page is for developers who want to build against EcoQuests. The plugin is open-source, so you can hook into it as a dependency and read the code directly.

## Source code

The full source is on [GitHub](https://github.com/Auxilor/EcoQuests).

## Adding the dependency

1. Add the Auxilor repository to your `build.gradle.kts`.
2. Add EcoQuests as a `compileOnly` dependency.

```kotlin
repositories {
    maven("https://repo.auxilor.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.willfp:EcoQuests:<version>")
}
```

The latest version available on the repo can be found [here](https://github.com/Auxilor/EcoQuests/tags).

<hr/>

## Where to go next

- **Shared APIs:** most cross-plugin APIs live in the [eco framework](https://github.com/Auxilor/eco).
- **Config side:** [How to make a quest](how-to-make-a-quest) covers the config that drives the plugin.