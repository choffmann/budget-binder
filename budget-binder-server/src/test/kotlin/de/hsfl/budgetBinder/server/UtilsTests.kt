package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.utils.formatToPeriod
import de.hsfl.budgetBinder.server.utils.isCreatedAndEndedInPeriod
import de.hsfl.budgetBinder.server.utils.parseParameterToLocalDateTimeOrErrorMessage
import java.time.LocalDateTime
import kotlin.test.*

class UtilsTests {
    private val errorString = "period has not the right pattern"

    @Test
    fun testParsePeriodCurrentTruePeriodNull() {
        val now = LocalDateTime.now()
        val shouldPeriod = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)

        val pair = parseParameterToLocalDateTimeOrErrorMessage(true, null)
        assertNull(pair.first)
        assertEquals(shouldPeriod, pair.second)
    }

    @Test
    fun testParsePeriodCurrentFalsePeriodNull() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, null)
        assertNull(pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodCurrentTruePeriodRight() {
        val now = LocalDateTime.now()
        val shouldPeriod = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)

        val pair = parseParameterToLocalDateTimeOrErrorMessage(true, "06-2022")
        assertNull(pair.first)
        assertEquals(shouldPeriod, pair.second)
    }

    @Test
    fun testParsePeriodCurrentTruePeriodFalse() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "65416546")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodWithCharakters() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "sjdvhkf434")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodMoreNumbers() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "65416546")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodWithoutDash() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "0135970")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodWithDashButWrongPlace() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "01359-0")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodWithDashRightPlaceWrongNumbers() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "34-0001")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodWrongMonth() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "34-2000")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodWrongYear() {
        val pair = parseParameterToLocalDateTimeOrErrorMessage(false, "01-1999")
        assertEquals(errorString, pair.first)
        assertNull(pair.second)
    }

    @Test
    fun testParsePeriodRightPatternMonth() {
        repeat(12) {
            val month = it + 1
            val shouldPeriod = LocalDateTime.of(2000, month, 1, 0, 0)
            val pair = parseParameterToLocalDateTimeOrErrorMessage(false, formatToPeriod(shouldPeriod))

            assertNull(pair.first)
            assertEquals(shouldPeriod, pair.second)
        }
    }

    @Test
    fun testParsePeriodRightPatternYear() {
        repeat(8000) {
            val year = it + 2000
            val shouldPeriod = LocalDateTime.of(year, 1, 1, 0, 0)
            val pair = parseParameterToLocalDateTimeOrErrorMessage(false, formatToPeriod(shouldPeriod))

            assertNull(pair.first)
            assertEquals(shouldPeriod, pair.second)
        }
    }

    @Test
    fun testIsCreatedAndEndedInPeriodAllNow() {
        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(!isCreatedAndEndedInPeriod(now, now, period))
    }

    @Test
    fun testIsCreatedAndEndedInPeriodCreatedNowEndedNullPeriodNow() {
        val now = LocalDateTime.now()
        val created = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(isCreatedAndEndedInPeriod(created, null, period))
    }

    @Test
    fun testIsCreatedAndEndedInPeriodCreatedNowEndedNullPeriodFuture() {
        val now = LocalDateTime.now().plusMonths(1)
        val created = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(isCreatedAndEndedInPeriod(created, null, period))
    }

    @Test
    fun testIsCreatedAndEndedInPeriodCreatedNowEndedNullPeriodPast() {
        val now = LocalDateTime.now().minusMonths(1)
        val created = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(!isCreatedAndEndedInPeriod(created, null, period))
    }

    @Test
    fun testIsCreatedAndEndedInPeriodCreatedNowEndedPastPeriodNow() {
        val now = LocalDateTime.now()
        val created = LocalDateTime.now()
        val ended = LocalDateTime.now().minusMonths(1)
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(!isCreatedAndEndedInPeriod(created, ended, period))
    }

    @Test
    fun testIsCreatedAndEndedInPeriodCreatedPastEndedNowPeriodNow() {
        val now = LocalDateTime.now()
        val created = LocalDateTime.now().minusMonths(1)
        val ended = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(!isCreatedAndEndedInPeriod(created, ended, period))
    }

    @Test
    fun testIsCreatedAndEndedInPeriodCreatedPastEndedNowPeriodPast() {
        val now = LocalDateTime.now().minusMonths(1)
        val created = LocalDateTime.now().minusMonths(1)
        val ended = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(isCreatedAndEndedInPeriod(created, ended, period))
    }

    @Test
    fun testIsCreatedAndEndedInPeriodCreatedPastEndedNowPeriodFuture() {
        val now = LocalDateTime.now().plusMonths(1)
        val created = LocalDateTime.now().minusMonths(1)
        val ended = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        assert(!isCreatedAndEndedInPeriod(created, ended, period))
    }
}
