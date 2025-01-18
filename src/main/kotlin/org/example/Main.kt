package org.example

import mu.KotlinLogging


private val logger = KotlinLogging.logger {}
fun main() {
    logger.info { "Starting bot." }
    setupBot().startPolling()
    logger.info { "Starting bot polling." }
}
