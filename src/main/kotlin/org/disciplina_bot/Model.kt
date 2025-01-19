package org.disciplina_bot

import java.time.DayOfWeek
import java.time.LocalTime

// TODO: encontrar um jeito melhor de criar propiedades de navegação reversas
data class BlocoHorario (
    val day: DayOfWeek,
    val start: LocalTime,
    val end: LocalTime,
) {
    lateinit var turma: Turma
    val normalizedEnd : Long
        get() {
            val dayIndex = day.ordinal.toLong()
            val startMinutes = end.toSecondOfDay() / 60;
            return dayIndex * 1440 + startMinutes
        }
    val normalizedStart : Long
        get() {
            val dayIndex = day.ordinal.toLong()
            val startMinutes = start.toSecondOfDay() / 60;
            return dayIndex * 1440 + startMinutes
        }

    override fun toString() = "BlocoHorario(day=$day, start=$start, end=$end)"

    infix fun intersects(other: BlocoHorario)
        = this.start <= other.end && other.start <= this.end
}

data class Turma(
    val id: String,
    val horarios: List<BlocoHorario>,
) {
    lateinit var disciplina: Disciplina
    override fun toString() = "Turma(id=$id, horarios=[${horarios.size} horarios])"
}

data class Disciplina(
    val id: String,
    val turmas: List<Turma>
)
