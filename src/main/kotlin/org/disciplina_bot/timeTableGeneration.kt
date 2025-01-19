package org.disciplina_bot

import java.time.DayOfWeek
fun generateCombinations(courses: List<Disciplina>): List<List<BlocoHorario>> {
    val turmaCombinations = courses
        .map { it.turmas }
        .fold(listOf(emptyList<Turma>())) { acc, turmas ->
            acc.flatMap { combination ->
                turmas.map { turma -> combination + turma }
            }
        }

    val validCombinations = mutableListOf<List<BlocoHorario>>()

    for (combination in turmaCombinations) {
        val horarios = combination
            .flatMap { it.horarios }
            .sortedBy { it.normalizedEnd }

        var hasConflict = false
        for (i in 0 until horarios.size - 1) {
            if (horarios[i] intersects horarios[i + 1]) {
                hasConflict = true
                break
            }
        }

        if (!hasConflict) {
            validCombinations.add(horarios)
        }
    }

    return validCombinations
}


fun getScheduleAsString(blocks: List<BlocoHorario>): String {
    val daysOfWeek = mapOf(
        DayOfWeek.MONDAY to "Segunda-feira",
        DayOfWeek.TUESDAY to "Terça-feira",
        DayOfWeek.WEDNESDAY to "Quarta-feira",
        DayOfWeek.THURSDAY to "Quinta-feira",
        DayOfWeek.FRIDAY to "Sexta-feira",
        DayOfWeek.SATURDAY to "Sábado"
    )

    val blocksByDay = blocks.groupBy { it.day }

    val scheduleString = StringBuilder()

    for (day in DayOfWeek.entries) {
        val dayName = daysOfWeek[day] ?: continue
        val dayBlocks = blocksByDay[day] ?: continue

        scheduleString.append("$dayName\n")
        for (block in dayBlocks) {
            scheduleString.append(" ${block.turma.disciplina.id} - Início: ${block.start} | Fim: ${block.end}\n")
        }
        scheduleString.append("\n")
    }

    return scheduleString.toString()
}


