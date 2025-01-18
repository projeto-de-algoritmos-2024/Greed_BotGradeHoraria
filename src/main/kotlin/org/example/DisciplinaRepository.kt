package org.example

import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

private const val URL = "jdbc:pgsql://localhost:5432/greed"
private val USER = System.getenv("DB_USER")
private val PASSWORD = System.getenv("DB_PASSWORD")
private val logger = KotlinLogging.logger {}

object HorariosView : Table("horarios") {
    val id = integer("id")
    // TODO: acertar tamanhos
    val codigo = varchar("codigo", 255)
    val codigodisciplina = varchar("codigodisciplina", 255)
    val turma = varchar("turma", 255)
}

class DisciplinaRepository {
    init {
        Database.connect(URL, "com.impossibl.postgres.jdbc.PGDriver", USER, PASSWORD)
    }

    fun buscarDisciplina(id: String): Disciplina {
        logger.info { "Starting search for course $id."}
        val turmas = mutableMapOf<String, MutableList<BlocoHorario>>()

        transaction {
            addLogger(StdOutSqlLogger)

            HorariosView
                .selectAll()
                .where { HorariosView.codigodisciplina eq id}
                .forEach {row ->
                    val bloco = convertCode(row[HorariosView.codigo])
                    val idTurma = row[HorariosView.turma]
                    val turma = turmas.getOrPut(idTurma) { mutableListOf() }
                    turma += bloco
                }
        }

        if (turmas.isEmpty()) {
            logger.error { "No entries found for course $id" }
            throw NoSuchElementException("No data found for course $id")
        }

        val disciplina = Disciplina(id, turmas.map { Turma(it.key, mergeBlocks(it.value)) })

        logger.info { "Fetched ${disciplina.turmas.size} sections, ${disciplina.turmas.flatMap { it.horarios }.size} blocks, for course ${disciplina.id}." }
        logger.info { "Sections: ${disciplina.turmas}, blocks ${disciplina.turmas.map { it.id to it.horarios}}."}

        return disciplina
    }
}


