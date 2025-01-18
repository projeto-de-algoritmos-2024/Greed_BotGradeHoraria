package org.example

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.util.ArrayDeque
import java.util.Deque
import kotlin.math.abs

private fun getTimePair(start: String, end: String)
    = Pair(LocalTime.parse(start), LocalTime.parse(end))

private val conversionTable: Map<String, Pair<LocalTime, LocalTime>> = mapOf(
    "M1" to getTimePair("08:00", "08:55"),
    "M2" to getTimePair("08:55", "09:50"),
    "M3" to getTimePair("10:00", "10:55"),
    "M4" to getTimePair("10:55", "11:50"),
    "M5" to getTimePair("12:00", "12:55"),

    "T1" to getTimePair("12:55", "13:50"),
    "T2" to getTimePair("14:00", "14:55"),
    "T3" to getTimePair("14:55", "15:50"),
    "T4" to getTimePair("16:00", "16:55"),
    "T5" to getTimePair("16:55", "17:50"),
    "T6" to getTimePair("18:00", "18:55"),

    "N1" to getTimePair("19:00", "19:50"),
    "N2" to getTimePair("19:50", "20:40"),
    "N3" to getTimePair("20:50", "21:40"),
    "N4" to getTimePair("21:40", "22:30"),
)

fun convertCode(code: String) : BlocoHorario {
    val day = DayOfWeek.of(code[0].digitToInt()-1)
    val time = conversionTable[code.substring(1)] ?: throw IllegalArgumentException("Wrong code format: $code")
    return BlocoHorario(day, time.first, time.second)
}

private fun mergeAdjacent(blocoA: BlocoHorario, blocoB: BlocoHorario) : BlocoHorario? {
    if (blocoA.day != blocoB.day)
        return null

    val gap = Duration.between(blocoA.end, blocoB.start).toMinutes().toInt()
    if (gap < 0 || gap > 10) return null

    return BlocoHorario(blocoA.day, blocoA.start, blocoB.end)
}

fun mergeBlocks(blocks: List<BlocoHorario>) : List<BlocoHorario> {
    if (blocks.isEmpty()) return emptyList()

    val sortedBlocks = blocks.sortedWith(compareBy({ it.day }, { it.start }))
    val stack = ArrayDeque<BlocoHorario>()

    stack.push(sortedBlocks.first())

    for (i in 1 until sortedBlocks.size) {
        val top = stack.peek()
        val current = sortedBlocks[i]

        val merged = mergeAdjacent(top, current)
        if (merged != null) {
            stack.pop()
            stack.push(merged)
        } else {
            stack.push(current)
        }
    }

    return stack.toList().reversed()
}

data class BlocoHorario (
    val day: DayOfWeek,
    val start: LocalTime,
    val end: LocalTime,
)