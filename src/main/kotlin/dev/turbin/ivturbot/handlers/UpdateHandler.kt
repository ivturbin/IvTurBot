package dev.turbin.ivturbot.handlers

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

interface UpdateHandler {
    fun supports(update: Update): Boolean
    fun handle(update: Update): SendMessage?
}