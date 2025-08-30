package dev.turbin.ivturbot

import dev.turbin.ivturbot.jooq.tables.Highday
import dev.turbin.ivturbot.jooq.tables.Recipient.RECIPIENT
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.time.LocalDate

@Component
//@ConditionalOnProperty(prefix = "options", name = ["notifications"], havingValue = "on")
class Notificator(val telegramBot: TelegramBot, val botRepository: BotRepository) {

    @Scheduled(cron = "0 0 6 * * ?")
    fun sendNotification() {

        val notifications = botRepository.getNotificationsForDate(LocalDate.now())

        if (notifications.isNotEmpty) {
            notifications.forEach {

                //Пока обрабатываем только дни рождения
                if (!it.get(Highday.HIGHDAY.HIGHDAY_TYPE).equals("BIRTHDAY")) {
                    return
                }

                val recipientTgChatId = it.get(RECIPIENT.TG_CHAT_ID).toString()

                val notification = SendMessage()
                notification.chatId = recipientTgChatId

                val tgUserId = it.get(Highday.HIGHDAY.TG_CHAT_ID)

                if (tgUserId != null) {
                    val getChatMember = GetChatMember(recipientTgChatId, tgUserId)
                    val getChatMemberResult = telegramBot.execute(getChatMember)

                    notification.text = "Сегодня празднует свой день рождения @${getChatMemberResult.user.userName}"
                } else {
                    notification.text = "Сегодня празднует свой день рождения " + it.get(Highday.HIGHDAY.DESCRIPTION);
                }

                telegramBot.execute(notification)
            }
        }
    }
}