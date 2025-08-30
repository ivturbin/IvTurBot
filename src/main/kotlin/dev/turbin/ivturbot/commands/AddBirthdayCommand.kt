package dev.turbin.ivturbot.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class AddBirthdayCommand : BotCommand {
    override val name = "/add_day"
    override fun execute(message: Message): SendMessage {
        // TODO("Not yet implemented")

        val chatId = message.chatId

        val response = SendMessage(chatId.toString(), "Пока не реализовано 🤯")

        return response
    }
}