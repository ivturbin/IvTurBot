package dev.turbin.ivturbot.entity

import dev.turbin.ivturbot.enums.HighdayType
import java.time.LocalDate

class HighdayEntity(val highdayType: HighdayType) {
    var description: String? = null
    var highdayDt: LocalDate? = null
}
