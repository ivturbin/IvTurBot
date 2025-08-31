package dev.turbin.ivturbot.handlers.commands

import dev.turbin.ivturbot.BotRepository
import dev.turbin.ivturbot.enums.Command
import dev.turbin.ivturbot.enums.NotificationType
import dev.turbin.ivturbot.jooq.tables.Recipient
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class StartCommand(private val botRepository: BotRepository) : CommandHandler {

    override val command = Command.START

    override fun handleCommand(message: Message): SendMessage {
        val chatId = message.chatId
        val response = SendMessage(chatId.toString(), "")

        response.text = "Привет, ${message.chat.userName ?: message.chat.title}!"

        val recipient = botRepository.getRecipient(chatId)
        if (recipient.isNotEmpty) {
            response.text += if (recipient[0].get(Recipient.RECIPIENT.NOTIFICATION_TYPE) == NotificationType.NO.name) {
                " Вы уже есть в списке, но уведомления выключены."
            } else {
                " Вы уже есть в списке, можете добавлять даты."
            }
        } else {
            botRepository.saveRecipient(chatId, message.chat.type, message.chat.userName ?: message.chat.title)
            response.text += " Запомнил вас, обратитесь к Ивану Турбину для активации."
        }

        return response
    }
}