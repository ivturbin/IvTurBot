package dev.turbin.ivturbot.service

import dev.turbin.ivturbot.entity.HighdayEntity
import dev.turbin.ivturbot.enums.HighdayType
import dev.turbin.ivturbot.enums.RecipientState
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class StateManager {
    private val states = ConcurrentHashMap<Long, RecipientState>()
    private val highdayDrafts = ConcurrentHashMap<Long, HighdayEntity>()

    fun getState(chatId: Long): RecipientState = states[chatId] ?: RecipientState.IDLE
    fun setState(chatId: Long, state: RecipientState) { states[chatId] = state }
    fun reset(chatId: Long) {
        states.remove(chatId)
        highdayDrafts.remove(chatId)
    }

    fun addHighdayDraft(chatId: Long, highdayType: HighdayType) {
        val highdayDraft = HighdayEntity(highdayType)
        highdayDrafts[chatId] = highdayDraft
    }

    fun getHighdayDraft(chatId: Long): HighdayEntity {
        return highdayDrafts[chatId]!!
    }
}
