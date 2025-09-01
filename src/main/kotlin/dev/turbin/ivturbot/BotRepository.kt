package dev.turbin.ivturbot

import dev.turbin.ivturbot.enums.HighdayType
import dev.turbin.ivturbot.enums.NotificationType
import dev.turbin.ivturbot.jooq.tables.Highday.HIGHDAY
import dev.turbin.ivturbot.jooq.tables.HighdayRecipient.HIGHDAY_RECIPIENT
import dev.turbin.ivturbot.jooq.tables.Recipient.RECIPIENT
import org.jooq.*
import org.jooq.impl.DSL

import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import java.time.LocalDate


@Repository
class BotRepository(private val dsl: DSLContext) {
    fun getNotificationsForDate(date: LocalDate) = dsl
        .select()
        .from(HIGHDAY)
        .join(HIGHDAY_RECIPIENT).on(HIGHDAY.HIGHDAY_ID.eq(HIGHDAY_RECIPIENT.HIGHDAY_ID))
        .join(RECIPIENT).on(HIGHDAY_RECIPIENT.RECIPIENT_ID.eq(RECIPIENT.RECIPIENT_ID))
        .where(
            day(HIGHDAY.HIGHDAY_DT).eq(date.dayOfMonth)
                .and(month(HIGHDAY.HIGHDAY_DT).eq(date.monthValue))
        )
        .fetch()

    fun getAllBirthdaysForChat(chatId: Long) = dsl
        .select(HIGHDAY.TG_CHAT_ID, HIGHDAY.DESCRIPTION, HIGHDAY.HIGHDAY_DT)
        .from(HIGHDAY)
        .join(HIGHDAY_RECIPIENT).on(HIGHDAY.HIGHDAY_ID.eq(HIGHDAY_RECIPIENT.HIGHDAY_ID))
        .join(RECIPIENT).on(HIGHDAY_RECIPIENT.RECIPIENT_ID.eq(RECIPIENT.RECIPIENT_ID))
        .where(RECIPIENT.TG_CHAT_ID.eq(chatId))
        .and(HIGHDAY.HIGHDAY_TYPE.eq("BIRTHDAY"))
        .orderBy(
            extract(HIGHDAY.HIGHDAY_DT, DatePart.MONTH),
            extract(HIGHDAY.HIGHDAY_DT, DatePart.DAY)
        )
        .fetch()

    fun getRecipient(chatId: Long) = dsl
        .select()
        .from(RECIPIENT)
        .where(RECIPIENT.TG_CHAT_ID.eq(chatId))
        .fetch()

    fun saveRecipient(chatId: Long, recipientType: String, description: String) = dsl
        .insertInto(RECIPIENT)
        .set(RECIPIENT.TG_CHAT_ID, chatId)
        .set(RECIPIENT.NOTIFICATION_TYPE, NotificationType.NO.name)
        .set(RECIPIENT.RECIPIENT_TYPE, recipientType)
        .set(RECIPIENT.DESCRIPTION, description)
        .execute()


    fun getNearestBirthdays(chatId: Long): Result<Record2<LocalDate, String>> {
        fun makeDate(year: Field<Int>, month: Field<Int>, day: Field<Int>): Field<LocalDate> =
            function("make_date", LocalDate::class.java, year, month, day)

        fun nextBirthdayExpr(birthDate: Field<LocalDate>): Field<LocalDate> {
            val currentYear = extract(currentDate(), DatePart.YEAR).cast(Int::class.java)
            val birthMonth  = extract(birthDate, DatePart.MONTH).cast(Int::class.java)
            val birthDay    = extract(birthDate, DatePart.DAY).cast(Int::class.java)

            val birthdayThisYear = makeDate(currentYear, birthMonth, birthDay)

            return `when`(birthdayThisYear.lt(currentLocalDate()),
                makeDate(currentYear.plus(inline(1)), birthMonth, birthDay)
            ).otherwise(birthdayThisYear)
        }

        val p  = HIGHDAY.`as`("p")
        val p2 = HIGHDAY.`as`("p2")

        // выражение для основного запроса
        val nextBirthdayExprP = nextBirthdayExpr(p.HIGHDAY_DT)

        // выражение для подзапроса (другой алиас)
        val nextBirthdayExprP2 = nextBirthdayExpr(p2.HIGHDAY_DT)
        val minNextBirthday = dsl.select(min(nextBirthdayExprP2)).from(p2)

        val result = dsl
            .select(p.HIGHDAY_DT, p.DESCRIPTION)
            .from(p)
            .join(HIGHDAY_RECIPIENT).on(p.HIGHDAY_ID.eq(HIGHDAY_RECIPIENT.HIGHDAY_ID))
            .join(RECIPIENT).on(HIGHDAY_RECIPIENT.RECIPIENT_ID.eq(RECIPIENT.RECIPIENT_ID))
            .where(RECIPIENT.TG_CHAT_ID.eq(chatId))
            .and(p.HIGHDAY_TYPE.eq(HighdayType.BIRTHDAY.name))
            .and(nextBirthdayExprP.eq(minNextBirthday))              // <- но в WHERE используем выражение без алиаса!
            .fetch()

        return result
    }
}