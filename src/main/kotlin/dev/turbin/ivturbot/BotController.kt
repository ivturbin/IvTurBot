package dev.turbin.ivturbot

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.time.LocalDate

@RestController
class BotController(private val bot: TelegramBot, private val botRepository: BotRepository) {

    @GetMapping("/test1")
    fun test1() {
        val sendMessage = SendMessage("1523054140", "test")
        bot.execute(sendMessage)
    }

    @GetMapping("/current-notifications")
    fun test2() {
        logger.info(botRepository.getNotificationsForDate(LocalDate.now()).toString())
    }


    companion object {
        private val logger = LoggerFactory.getLogger(TelegramBot::class.java)
    }
}