package com.willfp.ecoquests.quests

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * A reset schedule anchored to a wall-clock time rather than the last reset.
 */
internal class FixedResetSchedule private constructor(
    private val period: Period,
    private val time: LocalTime,
    private val dayOfWeek: DayOfWeek?,
    private val dayOfMonth: Int?,
    private val zone: ZoneId
) {
    fun previousResetMinute(now: Instant): Long {
        val zonedNow = now.atZone(zone)
        var candidate = candidateOnOrBefore(zonedNow)

        if (candidate.toInstant().isAfter(now)) {
            candidate = candidate.previousPeriod()
        }

        return candidate.toEpochSecond() / SECONDS_PER_MINUTE
    }

    fun nextResetMinute(now: Instant): Long {
        val zonedNow = now.atZone(zone)
        var candidate = candidateOnOrBefore(zonedNow)

        if (!candidate.toInstant().isAfter(now)) {
            candidate = candidate.nextPeriod()
        }

        return candidate.toEpochSecond() / SECONDS_PER_MINUTE
    }

    private fun candidateOnOrBefore(now: ZonedDateTime): ZonedDateTime {
        val date = when (period) {
            Period.DAILY -> now.toLocalDate()
            Period.WEEKLY -> {
                val daysSinceTarget =
                    (now.dayOfWeek.value - checkNotNull(dayOfWeek).value + DAYS_PER_WEEK) % DAYS_PER_WEEK
                now.toLocalDate().minusDays(daysSinceTarget.toLong())
            }
            Period.MONTHLY -> YearMonth.from(now).atDay(
                checkNotNull(dayOfMonth).coerceAtMost(YearMonth.from(now).lengthOfMonth())
            )
        }

        return ZonedDateTime.of(date, time, zone)
    }

    private fun ZonedDateTime.previousPeriod(): ZonedDateTime = when (period) {
        Period.DAILY -> minusDays(1)
        Period.WEEKLY -> minusWeeks(1)
        Period.MONTHLY -> inMonth(YearMonth.from(this).minusMonths(1))
    }

    private fun ZonedDateTime.nextPeriod(): ZonedDateTime = when (period) {
        Period.DAILY -> plusDays(1)
        Period.WEEKLY -> plusWeeks(1)
        Period.MONTHLY -> inMonth(YearMonth.from(this).plusMonths(1))
    }

    private fun inMonth(month: YearMonth): ZonedDateTime = ZonedDateTime.of(
        month.atDay(checkNotNull(dayOfMonth).coerceAtMost(month.lengthOfMonth())),
        time,
        zone
    )

    internal enum class Period {
        DAILY,
        WEEKLY,
        MONTHLY
    }

    companion object {
        private const val DAYS_PER_WEEK = 7
        private const val SECONDS_PER_MINUTE = 60L
        private val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        fun parse(
            type: String,
            time: String,
            day: String?,
            timezone: String?
        ): FixedResetSchedule {
            val period = runCatching {
                Period.valueOf(type.trim().uppercase(Locale.ROOT))
            }.getOrElse {
                throw IllegalArgumentException("type must be 'daily', 'weekly', or 'monthly'")
            }

            val parsedTime = try {
                LocalTime.parse(time.trim(), timeFormatter)
            } catch (_: DateTimeParseException) {
                throw IllegalArgumentException("time must use the 24-hour HH:mm format")
            }

            val parsedDayOfWeek = if (period == Period.WEEKLY) {
                val configuredDay = day?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("weekly schedules require a day")

                runCatching {
                    DayOfWeek.valueOf(configuredDay.trim().uppercase(Locale.ROOT))
                }.getOrElse {
                    throw IllegalArgumentException("day must be a weekday name, such as 'monday'")
                }
            } else {
                null
            }

            val parsedDayOfMonth = if (period == Period.MONTHLY) {
                val configuredDay = day?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("monthly schedules require a day")

                configuredDay.toIntOrNull()?.takeIf { it in 1..31 }
                    ?: throw IllegalArgumentException("day must be a number from 1 to 31 for monthly schedules")
            } else {
                null
            }

            val parsedZone = timezone
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    runCatching { ZoneId.of(it.trim()) }.getOrElse {
                        throw IllegalArgumentException("timezone must be a valid IANA timezone, such as 'Europe/London'")
                    }
                }
                ?: ZoneId.systemDefault()

            return FixedResetSchedule(period, parsedTime, parsedDayOfWeek, parsedDayOfMonth, parsedZone)
        }
    }
}
