package dev.turbin.ivturbot.handlers.commands

import dev.turbin.ivturbot.enums.Command
import dev.turbin.ivturbot.enums.HighdayType
import dev.turbin.ivturbot.enums.RecipientState
import dev.turbin.ivturbot.service.StateManager
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Component
class AddBirthdayCommand(private val stateManager: StateManager) : CommandHandler {
    override val command = Command.ADD_DAY

    override fun handleCommand(message: Message): SendMessage {

        stateManager.setState(message.chatId, RecipientState.CHOOSE_HIGHDAY_TYPE)

        val inlineKeyboardButton = InlineKeyboardButton("День рождения")
        inlineKeyboardButton.callbackData = HighdayType.BIRTHDAY.name

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = listOf(listOf(inlineKeyboardButton))

        val response = SendMessage(message.chatId.toString(),"Что сохраняем?")
        response.replyMarkup = inlineKeyboardMarkup

        return response
    }
}