package org.example

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}
private val buttonNames = mutableMapOf<Long, MutableList<String>>()
private val renamingButton = mutableMapOf<Long, Int>()
private val BOT_TOKEN = System.getenv("BOT_TOKEN")

private fun createDynamicKeyboard(chatId: Long): InlineKeyboardMarkup {

    val additionalButtons = listOf(
        InlineKeyboardButton.CallbackData("Resetar", "reset"),
        InlineKeyboardButton.CallbackData("Gerar Grades", "finalize")
    )

    val buttons = buttonNames.getOrPut(chatId) { mutableListOf() }
    val buttonRows = buttons.mapIndexed { index, label ->
        listOf(InlineKeyboardButton.CallbackData(label, "rename_button_$index"))
    }

    val addMoreButton = listOf(InlineKeyboardButton.CallbackData("Adicionar Disciplina", "add_button"))

    return InlineKeyboardMarkup.create(buttonRows + listOf(addMoreButton, additionalButtons))
}

fun setupBot() : Bot {
    logger.info { "Setting up bot." }
    val myBot = bot {
        token = BOT_TOKEN

        dispatch {
            command("start") {
                val chatId = message.chat.id
                buttonNames[chatId] = mutableListOf()
                renamingButton.remove(chatId)

                logger.info { "Starting chat $chatId." }
                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Clique no botão abaixo para adicionar mais disciplinas:",
                    replyMarkup = createDynamicKeyboard(chatId)
                )
            }

            callbackQuery {
                val chatId = callbackQuery.message!!.chat.id
                val data = callbackQuery.data

                when {
                    data == "add_button" -> {
                        val buttons = buttonNames.getOrPut(chatId) { mutableListOf() }
                        val buttonName = "Disciplina ${buttons.size + 1}"
                        buttons.add(buttonName)

                        logger.info { "Adding button: $buttonName at chat $chatId." }
                        bot.editMessageReplyMarkup(
                            chatId = ChatId.fromId(chatId),
                            messageId = callbackQuery.message!!.messageId,
                            replyMarkup = createDynamicKeyboard(chatId)
                        )
                    }

                    data == "reset" -> {
                        buttonNames[chatId]?.clear()
                        renamingButton.remove(chatId)
                        val messageId = callbackQuery.message!!.messageId
                        bot.deleteMessage(ChatId.fromId(chatId), messageId)
                        logger.info { "Resetting message at $chatId." }
                        val previousMessageId = messageId - 1
                        if (previousMessageId > 0) bot.deleteMessage(ChatId.fromId(chatId), previousMessageId)
                        bot.sendMessage(
                            chatId = ChatId.fromId(chatId),
                            text = "Todos as disciplinas foram resetadas.",
                            replyMarkup = createDynamicKeyboard(chatId)
                        )
                    }

                    data == "finalize" -> {
                        val buttons = buttonNames[chatId] ?: emptyList()
                        val buttonsString =
                            if (buttons.isEmpty()) "Nenhuma disciplina escolhida." else buttons.joinToString(", ")
                        bot.sendMessage(
                            chatId = ChatId.fromId(chatId),
                            text = "Lista de disciplinas: $buttonsString"
                        )
                        logger.info { "Finalizing timetable generation for $chatId. Selected buttons: $buttonsString" }
                        logger.warn { "Timetable generation not implemented." }
//                        TODO ADICIONAR A GERAÇÃO DE GRADES AQUI
                    }

                    data.startsWith("rename_button_") -> {
                        val buttonIndex = data.removePrefix("rename_button_").toInt()
                        renamingButton[chatId] = buttonIndex

                        logger.info { "Renaming button ${buttonIndex + 1} at chat $chatId." }
                        bot.sendMessage(
                            chatId = ChatId.fromId(chatId),
                            text = "Digite o código da disciplina ${buttonIndex + 1}:"
                        )
                    }
                }
            }

            text {
                val chatId = message.chat.id
                val text = message.text ?: return@text

                if (chatId in renamingButton) {
                    val buttonIndex = renamingButton[chatId]!!
                    buttonNames[chatId]?.let { buttons ->
                        if (buttonIndex in buttons.indices) {
                            buttons[buttonIndex] = text

                            val messageId = message.messageId
                            (messageId downTo (messageId - 2)).forEach { id ->
                                bot.deleteMessage(ChatId.fromId(chatId), id)
                            }

                            logger.info { "Button $buttonIndex at chat $chatId to $text." }
                            bot.sendMessage(
                                chatId = ChatId.fromId(chatId),
                                text = "A Disciplina ${buttonIndex + 1} foi renomeado para \"$text\".",
                                replyMarkup = createDynamicKeyboard(chatId)
                            )
                        }
                    }

                    renamingButton.remove(chatId)
                }
            }
        }
    }
    logger.info { "Finished bot setup" }
    return myBot
}


