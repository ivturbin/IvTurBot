package dev.turbin.ivturbot.handlers

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ChatMemberUpdate : UpdateHandler {
    private val logger = LoggerFactory.getLogger(ChatMemberUpdate::class.java)

    override fun supports(update: Update): Boolean =
        update.myChatMember != null

    override fun handle(update: Update): SendMessage? {
        val member = update.myChatMember

        logger.info(
            "Chat action: ${member.chat.title}(${member.chat.id}), " +
                    "${member.newChatMember.status} by ${member.from.userName} " +
                    "(${member.from.firstName} ${member.from.lastName})"
        )

        return null
    }
}