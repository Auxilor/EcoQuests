package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.tasks.Task

interface TaskEvent {
    val task: Task
}
