package com.example.periodtracker.data

import android.content.Context
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun calculatePhaseLengths(context: Context) {
    //based of study conducted on 600,000 women
    // https://pmc.ncbi.nlm.nih.gov/articles/PMC6710244/table/Tab1/

    val cycleLength = UserData.getCycleLength(context)
    val menstrualLength = UserData.getMenstrualLength(context)
    val ovulationLength = UserData.getOvulationLength(context)
    var follicularLength = UserData.getFollicularLength(context)
    var lutealLength = UserData.getLutealLength(context)

    val baseFollicularLength = when (cycleLength) {
        in 15..20 -> 10
        in 21..24 -> 12
        in 25..30 -> 15
        in 31..35 -> 20
        in 36..50 -> 27
        else -> 17
    }

    follicularLength = baseFollicularLength + ( 4 - menstrualLength)

    lutealLength = cycleLength - (follicularLength+menstrualLength+ovulationLength)

    //statistically, the shortest luteal phase is 6 days. when cycle length increases, that statistical low increase
    //need to make sure the lutealLength calculation is not lower than the statistical minLuteal length
    var follicularLutealDifference = 0

    val minLuteal = when (cycleLength) {
        in 15..20 -> 6 //8+-2
        in 21..24 -> 9
        in 25..30 -> 10
        in 31..35 -> 11
        in 36..50 -> 11
        else -> 6 //statistically the shortest it can be
    }

    if (lutealLength < minLuteal) {
        follicularLutealDifference = minLuteal - lutealLength
        lutealLength = minLuteal
    }

    follicularLength -= follicularLutealDifference


    UserData.saveLutealLength(context,lutealLength)
    UserData.saveFollicularLength(context,follicularLength)
}

fun calculateDaysTillNextPeriod(context:Context) {
    val cycleLength = UserData.getCycleLength(context)

    //Days until next period
    val today = LocalDate.now()
    //lastPeriod needs to be updated each time after a period happens so that days since stays updated.
    val lastPeriod = UserData.getLastPeriodEnd(context)?.let { epochMs ->
        Instant.ofEpochMilli(epochMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    val daysSincePeriod = ChronoUnit.DAYS.between(lastPeriod,today)

    val daysUntilNextPeriod = cycleLength - daysSincePeriod

    UserData.saveDaysUntilMenstruation(context,daysUntilNextPeriod.toInt())
}