package org.disciplina_bot

import java.time.Duration
import java.util.ArrayDeque

private fun mergeAdjacent(blocoA: BlocoHorario, blocoB: BlocoHorario) : BlocoHorario? {
    // TODO: fazer checagem com require
//    if (blocoA.turma != blocoB.turma) throw IllegalArgumentException("Can't merge blocks from different sections.")

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