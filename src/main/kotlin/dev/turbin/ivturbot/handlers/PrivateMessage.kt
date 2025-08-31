package dev.turbin.ivturbot.handlers

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class PrivateMessage : UpdateHandler{
    override fun supports(update: Update): Boolean {
        return update.message?.chat?.type == "private" && update.message?.isCommand == false
    }

    override fun handle(update: Update): SendMessage {
        return SendMessage(update.message.chatId.toString(), "Остановись, я всё ещё пока что ничего не умею 🙃")
    }
}