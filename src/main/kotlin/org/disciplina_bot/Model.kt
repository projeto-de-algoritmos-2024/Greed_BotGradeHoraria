package org.disciplina_bot

data class Turma(
    val id: String,
    val horarios: List<BlocoHorario>
)

data class Disciplina(
    val id: String,
    val turmas: List<Turma>
)