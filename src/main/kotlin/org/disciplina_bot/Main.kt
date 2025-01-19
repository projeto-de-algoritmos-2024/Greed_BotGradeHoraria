package org.disciplina_bot

import mu.KotlinLogging
import java.time.DayOfWeek


private val logger = KotlinLogging.logger {}

fun main() {
    setupBot().startPolling()
}