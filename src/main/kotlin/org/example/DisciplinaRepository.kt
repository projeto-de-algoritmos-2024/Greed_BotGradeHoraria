package org.example

import mu.KotlinLogging
import java.sql.Connection
import java.sql.DriverManager

private const val URL = "jdbc:postgresql://localhost:5432/greed"
private val USER = System.getenv("DB_USER")
private val PASSWORD = System.getenv("DB_PASSWORD")
private val logger = KotlinLogging.logger {}

data class DisciplinaInfo(
    val horariosID: MutableList<Int>,
    val horariosSTR: MutableList<String>,
    val codigodisciplina: String,
    val turma: String
)

class DisciplinaRepository : AutoCloseable {
    private val connection = DriverManager.getConnection(URL, USER, PASSWORD)

    fun buscarDisciplina(id: String): List<DisciplinaInfo> {
        logger.info { "Starting search for course $id."}
        val query = """
            SELECT *
            FROM horarios
            WHERE codigodisciplina = ?
        """

        val statement = connection.prepareStatement(query)
        statement.setString(1, id)

        val resultSet = statement.executeQuery()

        val disciplinas = mutableListOf<DisciplinaInfo>()
        while (resultSet.next()) {
            val horarioID = resultSet.getInt("id")
            val horarioSTR = resultSet.getString("codigo")
            val disciplinaOfertada = resultSet.getString("codigodisciplina")
            val turma = resultSet.getString("turma")

            val existente = disciplinas.find { it.codigodisciplina == disciplinaOfertada && it.turma == turma }
            if (existente != null) {
                existente.horariosID.add(horarioID)
                existente.horariosSTR.add(horarioSTR)
            } else {
                disciplinas.add(
                    DisciplinaInfo(
                        horariosID = mutableListOf(horarioID),
                        horariosSTR = mutableListOf(horarioSTR),
                        codigodisciplina = disciplinaOfertada,
                        turma = turma
                    )
                )
            }
        }

        resultSet.close()
        statement.close()
        logger.info { "Fetched ${disciplinas.size} entries." }
        logger.debug { "Entries: ${disciplinas.joinToString(", ")}" }
        return disciplinas
    }

    fun escolherTodasDisciplinas(ids: List<String>): List<List<DisciplinaInfo>> {
        logger.info { "Starting search for courses ${ids.joinToString(", ")}" }
        val matrizDisciplinas = mutableListOf<List<DisciplinaInfo>>()
        for (id in ids) matrizDisciplinas.add(buscarDisciplina(id))
        logger.info { "Finished fetching entries for courses ${ids.joinToString(", ")}" }
        return matrizDisciplinas
    }

    override fun close() {
        connection.close()
    }

}


