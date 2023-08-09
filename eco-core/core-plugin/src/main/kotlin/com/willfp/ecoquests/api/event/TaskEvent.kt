package com.willfp.ecoquests.api.event

import com.willfp.ecoquests.tasks.TaskTemplate

interface TaskEvent {
    val task: TaskTemplate
}
