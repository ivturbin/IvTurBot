package dev.turbin.ivturbot.handlers.commands

import org.springframework.stereotype.Component

@Component
class CommandRegistry(commands: List<CommandHandler>) {
    private val commandMap: Map<String, CommandHandler> = commands.associateBy { it.command.command }

    fun findCommandHandler(command: String): CommandHandler? = commandMap[command]
}