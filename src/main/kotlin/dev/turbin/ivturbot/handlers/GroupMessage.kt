package dev.turbin.ivturbot.handlers

import dev.turbin.ivturbot.entity.HighdayEntity
import dev.turbin.ivturbot.enums.HighdayType
import dev.turbin.ivturbot.enums.RecipientState
import dev.turbin.ivturbot.service.StateManager
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.time.LocalDate

@Component
class GroupMessage(
    private val stateManager: StateManager
) : UpdateHandler {
    override fun supports(update: Update): Boolean {
        return update.hasMessage() &&
                update.message.hasText() &&
                update.message.chat.type == "supergroup" && !update.message.isCommand
    }

    override fun handle(update: Update): SendMessage? {
        val message = update.message
        val state = stateManager.getState(message.chatId)

        if (state == RecipientState.ADD_HIGHDAY_DESCRIPTION) {
            val highdayDraft = stateManager.getHighdayDraft(message.chatId)
            highdayDraft.description = update.message.text
            stateManager.setState(message.chatId, RecipientState.ADD_HIGHDAY_DATE)
            return SendMessage(message.chatId.toString(), "Введите дату в формате ДД.ММ, либо ДД.ММ.ГГГГ")
        }

        if (state == RecipientState.ADD_HIGHDAY_DATE) {
            val text = update.message.text
            var date: LocalDate? = null

            if (Regex("^[0-9]{2}.[0-9]{2}.[0-9]{4}$").matches(text)) {
                val split = text.split(".")
                date = LocalDate.of(split[2].toInt(), split[1].toInt(), split[0].toInt())
            } else if (Regex("^[0-9]{2}.[0-9]{2}$").matches(text)) {
                val split = text.split(".")
                date = LocalDate.of(LocalDate.now().year, split[1].toInt(), split[0].toInt())
            } else {
                return SendMessage(update.message.chatId.toString(), "Ошибка. Введите дату в формате ДД.ММ или ДД.ММ.ГГГГ")
            }

            val highdayDraft = stateManager.getHighdayDraft(message.chatId)

            highdayDraft.highdayDt = date

            val okButton = InlineKeyboardButton("Ок, сохраняем")
            okButton.callbackData = "OK"

            val cancelButton = InlineKeyboardButton("Отмена")
            cancelButton.callbackData = "CANCEL"

            val inlineKeyboardMarkup = InlineKeyboardMarkup()
            inlineKeyboardMarkup.keyboard = listOf(listOf(okButton, cancelButton))

            val response = SendMessage(message.chatId.toString(),
                "Проверяем, вы ввели: ${highdayDraft.highdayType.russinanName}, ${highdayDraft.highdayDt}, ${highdayDraft.description}")
            response.replyMarkup = inlineKeyboardMarkup
            return response
        }

        return null

    }

}