package dev.turbin.ivturbot.commands

import dev.turbin.ivturbot.BotRepository
import dev.turbin.ivturbot.jooq.tables.Highday
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class BirthdaysCommand(private val botRepository: BotRepository) : BotCommand {
    override val name = "/birthdays"

    override fun execute(message: Message): SendMessage {
        val chatId = message.chatId
        val response = SendMessage(chatId.toString(), "")

        val birthdays = botRepository.getAllBirthdaysForChat(chatId)
        if (birthdays.isEmpty()) {
            response.text = "У вас нет сохранённых дней рождения."
        } else {
            response.parseMode = ParseMode.MARKDOWN
            response.text = "*Дни рождения:*\n" +
                    birthdays.joinToString("\n") {
                        String.format(
                            "%02d.%02d  %s",
                            it.get(Highday.HIGHDAY.HIGHDAY_DT).dayOfMonth,
                            it.get(Highday.HIGHDAY.HIGHDAY_DT).monthValue,
                            it.get(Highday.HIGHDAY.DESCRIPTION)
                        )
                    }
        }

        return response
    }
}