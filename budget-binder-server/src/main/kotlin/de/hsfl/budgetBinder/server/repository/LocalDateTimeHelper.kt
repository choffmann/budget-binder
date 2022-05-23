package de.hsfl.budgetBinder.server.repository

import java.time.LocalDateTime

fun isCreatedAndEndedCorrectPeriod(created: LocalDateTime, ended: LocalDateTime?, period: LocalDateTime): Boolean {
    val createdTime = LocalDateTime.of(created.year, created.month.value, 1, 0, 0, 0)
    val endedTime = ended?.let { LocalDateTime.of(it.year, it.month.value, 1, 0, 0, 0) }
    return createdTime <= period && (endedTime == null || endedTime > period)
}

fun parseParameterToLocalDateTime(current: Boolean, param: String?): Pair<String?, LocalDateTime?> {
    if (current) {
        val now = LocalDateTime.now()
        return null to LocalDateTime.of(now.year, now.month.value, 1, 0, 0, 0)
    }

    if (param == null)
        return null to null

    if (!param.matches("^(0?[1-9]|1[012])-([2-9]\\d[1-9]\\d|[1-9]\\d)\$".toRegex())) {
        return "period has not the right pattern" to null
    }

    val (month, year) = param.split("-")
    return null to LocalDateTime.of(year.toInt(), month.toInt(), 1, 0, 0, 0)
}
