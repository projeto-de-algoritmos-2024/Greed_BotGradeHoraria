package org.example

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId

fun main() {
    val bot = bot {
        token  = System.getenv("BOT_TOKEN")
        dispatch {
            text { bot.sendMessage(ChatId.fromId(message.chat.id), text = "Hello World!") }
        }
    }

    bot.startPolling()
}