package dev.turbin.ivturbot.commands

import org.springframework.stereotype.Component

@Component
class CommandRegistry(commands: List<BotCommand>) {
    private val commandMap: Map<String, BotCommand> = commands.associateBy { it.name }

    fun findCommand(command: String): BotCommand? = commandMap[command]
}