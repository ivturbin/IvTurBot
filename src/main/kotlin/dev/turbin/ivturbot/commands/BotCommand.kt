package dev.turbin.ivturbot.commands

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

interface BotCommand {
    val name: String
    fun execute(message: Message): SendMessage
}