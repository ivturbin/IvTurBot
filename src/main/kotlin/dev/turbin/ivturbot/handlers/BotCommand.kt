package dev.turbin.ivturbot.handlers

import dev.turbin.ivturbot.handlers.commands.CommandRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class BotCommand(@Value("\${telegram.bot.username}") private val username: String,
    private val commandRegistry: CommandRegistry) : UpdateHandler {

    override fun supports(update: Update): Boolean {
        val message = update.message ?: return false
        if (!message.hasText() || !message.isCommand) return false

        // Исключить обработку команд других ботов в группе
        if (update.message.chat.type != "private") {
            val commandBot = message.text.substringAfter("@", "")
            if (commandBot.isNotEmpty() && commandBot != username.substring(1)) {
                return false
            }
        }
        return true
    }

    override fun handle(update: Update): SendMessage {
        val commandHandler = commandRegistry.findCommandHandler(update.message.text.split("@")[0])
        return commandHandler?.handleCommand(update.message) ?: SendMessage(
            update.message.chatId.toString(),
            "Такой команды у меня пока нет 🥴"
        )
    }
}