package dev.turbin.ivturbot.commands

import dev.turbin.ivturbot.BotRepository
import dev.turbin.ivturbot.jooq.tables.Highday
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class NearestBirthdayCommand(private val botRepository: BotRepository) : BotCommand {

    override val name = "/nearest_birthday"

    override fun execute(message: Message): SendMessage {
        val chatId = message.chatId
        val birthdays = botRepository.getNearestBirthdays(chatId)

        val response = SendMessage(chatId.toString(), "")

        if (birthdays.isEmpty()) {
            response.text = "У вас нет сохранённых дней рождения."
        } else {
            response.text = "Ближайший день рождения, ${birthdays[0].get(Highday.HIGHDAY.HIGHDAY_DT).dayOfMonth}." +
                    "${birthdays[0].get(Highday.HIGHDAY.HIGHDAY_DT).monthValue}, ".padStart(4, '0') +
                    if (birthdays.size > 1) {"празднуют:\n"} else {"празднует "} +
                    birthdays.joinToString("\n") {
                        String.format(
                                "%s",
                                it.get(Highday.HIGHDAY.DESCRIPTION),
                            )
                    }
        }

        return response
    }
}