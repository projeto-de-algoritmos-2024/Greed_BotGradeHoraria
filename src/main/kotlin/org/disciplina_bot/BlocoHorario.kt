package org.disciplina_bot

import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.util.ArrayDeque

data class BlocoHorario (
    val day: DayOfWeek,
    val start: LocalTime,
    val end: LocalTime,
) {
    val normalizedEnd : Long
    get() {
        val dayIndex = day.ordinal.toLong()
        val startMinutes = end.toSecondOfDay() / 60;
        return dayIndex * 1440 + startMinutes
    }
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