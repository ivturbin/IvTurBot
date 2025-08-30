package dev.turbin.ivturbot

import dev.turbin.ivturbot.commands.CommandRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


@Component
class TelegramBot(
    @Value("\${telegram.bot.username}") private val username: String,
    @Value("\${telegram.bot.token}") private val token: String,
    private val commandRegistry: CommandRegistry
) : TelegramLongPollingBot(token) {

    override fun onUpdateReceived(update: Update) {

        if (update.myChatMember != null) {
            logger.info("Chat action: ${update.myChatMember.chat.title}(${update.myChatMember.chat.id}), ${update.myChatMember.newChatMember.status} by ${update.myChatMember.from.userName}  (${update.myChatMember.from.firstName} ${update.myChatMember.from.lastName})")
            return
        }

        if (update.hasMessage() && update.message.hasText()) {

            val message = update.message
            val chatId = message.chatId

            val response = when {
                message.isCommand -> {

                    val splitMessageText = message.text.split("@")
                    val command = splitMessageText[0]

                    // Исключить обработку команд других ботов в группе
                    if (message.chat.type != "private") {
                        val botOfCommand = splitMessageText[1]
                        if (botOfCommand != username.substring(1)) {
                            return
                        }
                    }

                    commandRegistry.findCommand(command)?.execute(message)
                }
                message.chat.type == "private" -> {
                    SendMessage(chatId.toString(), "Остановись, я всё ещё пока что ничего не умею 🙃")
                }
                message.entities?.any { it.type == "mention" && it.text.equals(username)} == true -> {
                    SendMessage(chatId.toString(), "${message.from.firstName}, я обрабатываю только команды.")
                }
                else -> null
            }

            response?.let {
                try {
                    execute(it)
                } catch (e: TelegramApiException) {
                    logger.error("Ошибка отправки сообщения", e)
                }
            }
        }
    }

    init {
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        botsApi.registerBot(this)
        logger.info("Bot instantiated")
    }

    override fun getBotUsername(): String {
        return username
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TelegramBot::class.java)
    }

}