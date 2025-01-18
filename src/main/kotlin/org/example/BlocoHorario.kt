package org.example

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.FileNotFoundException
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.util.ArrayDeque

private fun getTimePair(start: String, end: String)
    = Pair(LocalTime.parse(start), LocalTime.parse(end))

private val conversionTable = getConversionTable()

fun getConversionTable() : Map<String, Pair<LocalTime, LocalTime>> {
    val jsonTable = object {}.javaClass.getResource("/timetable.json")?.readText() ?: throw FileNotFoundException("Could not find timetable file")
    val rawTable: Map<String, List<String>> = Json.decodeFromString(jsonTable)
    return rawTable.mapValues { (_, times) ->
       getTimePair(times[0], times[1])
    }
}

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