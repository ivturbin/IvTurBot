package dev.turbin.ivturbot.handlers

import dev.turbin.ivturbot.BotRepository
import dev.turbin.ivturbot.enums.HighdayType
import dev.turbin.ivturbot.enums.RecipientState
import dev.turbin.ivturbot.jooq.tables.Recipient
import dev.turbin.ivturbot.service.StateManager
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class InlineKeyboardPress(
    private val stateManager: StateManager,
    private val botRepository: BotRepository
) : UpdateHandler {
    override fun supports(update: Update): Boolean {
        return !update.hasMessage() && update.hasCallbackQuery()
    }

    override fun handle(update: Update): SendMessage? {
        if (update.callbackQuery.data == HighdayType.BIRTHDAY.name) {
            stateManager.setState(update.callbackQuery.message.chatId, RecipientState.ADD_HIGHDAY_DESCRIPTION)
            stateManager.addHighdayDraft(update.callbackQuery.message.chatId, HighdayType.BIRTHDAY)
            return SendMessage(update.callbackQuery.message.chatId.toString(), "Ага, оформляем ДР. Напиши имя, например, \"Иван Мягков\"")
        }

        if (update.callbackQuery.data == "OK") {
            val highdayId = botRepository.saveHighday(stateManager.getHighdayDraft(update.callbackQuery.message.chatId))
            val recipientId = botRepository.getRecipient(update.callbackQuery.message.chatId)[0].get(Recipient.RECIPIENT.RECIPIENT_ID)
            botRepository.saveHighdayRecipient(highdayId.toLong(), recipientId)
            stateManager.reset(update.callbackQuery.message.chatId)
            return SendMessage(update.callbackQuery.message.chatId.toString(), "Дата сохранена. Можете проверить обновление командой /birthdays")
        }

        if (update.callbackQuery.data == "CANCEL") {
            stateManager.reset(update.callbackQuery.message.chatId)
            return SendMessage(update.callbackQuery.message.chatId.toString(), "Дата не сохранена. Вы всегда можете вызвать команду /add_day позже")
        }
        return null
    }
}