package org.example

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConfig {
    private const val URL = "jdbc:postgresql://localhost:5432/greed"
    private const val USER = "kd_user"
    private const val PASSWORD = "pa"

    fun getConnection(): Connection {
        return DriverManager.getConnection(URL, USER, PASSWORD)
    }
}

data class DisciplinaInfo(
    val horariosID: MutableList<Int>,
    val horariosSTR: MutableList<String>,
    val codigodisciplina: String,
    val turma: String
)

class Database {
    private val connection = DatabaseConfig.getConnection()

    fun buscarDisciplina(id: String): List<DisciplinaInfo> {
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
        return disciplinas
    }

    fun escolherTodasDisciplinas(ids: List<String>): List<List<DisciplinaInfo>> {
        val matrizDisciplinas = mutableListOf<List<DisciplinaInfo>>()
        for (id in ids) matrizDisciplinas.add(buscarDisciplina(id))
        return matrizDisciplinas
    }

}


