package com.bclymer.dailybudget.extensions

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by brianclymer on 7/29/16.
 * Copyright Travefy, Inc.
 */

fun Date.add(number: Long, unit: TimeUnit): Date {
    return Date(this.time + unit.toMillis(number))
}

val Date.atMidnight: Date by lazy {
    val cal = GregorianCalendar()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.time
}

fun Date.isSameDay(otherDay: Date): Boolean {
    val day1Cal = GregorianCalendar()
    day1Cal.time = this
    val day2Cal = GregorianCalendar()
    day2Cal.time = otherDay
    return day1Cal.get(Calendar.YEAR) == day2Cal.get(Calendar.YEAR) && day1Cal.get(Calendar.DAY_OF_YEAR) == day2Cal.get(Calendar.DAY_OF_YEAR)
}

fun Date.daysUntil(endDate: Date): Long {
    val startCal = GregorianCalendar()
    startCal.time = this
    val endCal = GregorianCalendar()
    endCal.time = endDate
    return getDaysBetweenDates(startCal, endCal)
}

private fun getDaysBetweenDates(startCal: Calendar?, endCal: Calendar?): Long {
    if (startCal == null || endCal == null) {
        return java.lang.Long.MIN_VALUE
    }
    setCalendarToBeginningOfDay(startCal)
    setCalendarToBeginningOfDay(endCal)
    endCal.set(Calendar.MINUTE, 1) // make sure it's 1 minute past, in case seconds make a difference.
    val diff = endCal.timeInMillis - startCal.timeInMillis
    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
}

private fun setCalendarToBeginningOfDay(calendar: Calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
}