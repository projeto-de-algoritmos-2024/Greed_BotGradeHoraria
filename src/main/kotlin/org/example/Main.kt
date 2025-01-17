package org.example

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

val buttonNames = mutableMapOf<Long, MutableList<String>>()
val renamingButton = mutableMapOf<Long, Int>()

fun main() {
    val bot = bot {
        token = System.getenv("BOT_TOKEN")

        dispatch {
            command("start") {
                val chatId = message.chat.id
                buttonNames[chatId] = mutableListOf()
                renamingButton.remove(chatId)

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
                        buttons.add("Disciplina ${buttons.size + 1}")

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
                        val buttonsString = if (buttons.isEmpty()) "Nenhuma disciplina escolhida." else buttons.joinToString(", ")
                        bot.sendMessage(
                            chatId = ChatId.fromId(chatId),
                            text = "Lista de disciplinas: $buttonsString"
                        )
//                        TODO ADICIONAR A GERAÇÃO DE GRADES AQUI
                    }

                    data != null && data.startsWith("rename_button_") -> {
                        val buttonIndex = data.removePrefix("rename_button_").toInt()
                        renamingButton[chatId] = buttonIndex

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

    bot.startPolling()
}

fun createDynamicKeyboard(chatId: Long): InlineKeyboardMarkup {

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
