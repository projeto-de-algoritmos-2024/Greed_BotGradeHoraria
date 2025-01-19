package org.disciplina_bot

import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.time.DayOfWeek
import java.time.LocalTime


private fun getTimePair(start: String, end: String)
        = Pair(LocalTime.parse(start), LocalTime.parse(end))

private val conversionTable = getConversionTable()

private fun getConversionTable() : Map<String, Pair<LocalTime, LocalTime>> {
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

