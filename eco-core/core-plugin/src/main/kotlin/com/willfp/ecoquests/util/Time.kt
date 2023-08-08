package com.willfp.ecoquests.util

const val MINUTES_IN_HOUR = 60
const val HOURS_IN_DAY = 24
const val DAYS_IN_MONTH = 30

fun formatDuration(minutes: Int): String {
    if (minutes < 0) return "Invalid Duration"


    val totalHours = minutes / MINUTES_IN_HOUR
    val totalDays = totalHours / HOURS_IN_DAY
    val totalMonths = totalDays / DAYS_IN_MONTH

    val remainingMinutes = minutes % MINUTES_IN_HOUR
    val remainingHours = totalHours % HOURS_IN_DAY
    val remainingDays = totalDays % DAYS_IN_MONTH

    val parts = mutableListOf<String>()

    if (totalMonths > 0) {
        parts.add("${totalMonths}mo")
    }

    if (remainingDays > 0) {
        parts.add("${remainingDays}d")
    }

    if (remainingHours > 0) {
        parts.add("${remainingHours}h")
    }

    parts.add("${remainingMinutes}m")

    return parts.joinToString(" ")
}
