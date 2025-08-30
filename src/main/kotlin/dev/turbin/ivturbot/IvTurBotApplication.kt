package dev.turbin.ivturbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class IvTurBotApplication

fun main(args: Array<String>) {
    runApplication<IvTurBotApplication>(*args)
}
