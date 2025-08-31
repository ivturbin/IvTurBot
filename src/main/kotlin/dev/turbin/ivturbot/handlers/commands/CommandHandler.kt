package dev.turbin.ivturbot.handlers.commands

import dev.turbin.ivturbot.enums.Command
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

interface CommandHandler {
    val command: Command

    fun handleCommand(message: Message) : SendMessage
}