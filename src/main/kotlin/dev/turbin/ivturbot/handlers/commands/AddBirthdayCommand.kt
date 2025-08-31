package dev.turbin.ivturbot.handlers.commands

import dev.turbin.ivturbot.enums.Command
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class AddBirthdayCommand(@Value("\${telegram.bot.username}") private val username: String) : CommandHandler {
    override val command = Command.ADD_DAY

    override fun handleCommand(message: Message): SendMessage {
        // TODO("Not yet implemented")

        val response = SendMessage(message.chatId.toString(), "Пока не реализовано 🤯")

        return response
    }
}