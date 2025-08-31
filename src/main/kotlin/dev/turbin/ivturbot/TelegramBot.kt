package dev.turbin.ivturbot

import dev.turbin.ivturbot.handlers.UpdateHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


@Component
class TelegramBot(
    @Value("\${telegram.bot.username}") private val username: String,
    @Value("\${telegram.bot.token}") private val token: String,
    private val updateHandlers: List<UpdateHandler>
) : TelegramLongPollingBot(token) {

    override fun onUpdateReceived(update: Update) {

        val response = updateHandlers
            .firstNotNullOfOrNull { handler ->
                handler.takeIf { it.supports(update) }?.handle(update)
            }

        response?.let {
            try {
                execute(it)
            } catch (e: TelegramApiException) {
                logger.error("Ошибка отправки сообщения", e)
            }
        }
    }

    init {
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(this)
        logger.info("Bot instantiated")
    }

    override fun getBotUsername(): String = username

    companion object {
        private val logger = LoggerFactory.getLogger(TelegramBot::class.java)
    }
}