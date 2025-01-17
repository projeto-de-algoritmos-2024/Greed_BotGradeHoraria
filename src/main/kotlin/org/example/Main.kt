package org.example

import mu.KotlinLogging


private val logger = KotlinLogging.logger {}
fun main() {
    println(DisciplinaRepository().escolherTodasDisciplinas(listOf("FGA0204", "FGA0214")))
    logger.info { "Starting bot." }
    setupBot().startPolling()
    logger.info { "Starting bot polling." }
}
