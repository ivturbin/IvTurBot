package dev.turbin.ivturbot.handlers

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ChatMention(@Value("\${telegram.bot.username}") private val username: String) : UpdateHandler{
    override fun supports(update: Update): Boolean {
        return update.message?.chat?.type != "private" &&
                update.message?.entities?.any { it.type == "mention" && it.text.equals(username)} == true
    }

    override fun handle(update: Update): SendMessage {
        return SendMessage(update.message.chatId.toString(),
            "${update.message.from.firstName}, я обрабатываю только команды.")
    }
}