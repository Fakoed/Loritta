package net.perfectdreams.loritta.tables

import org.jetbrains.exposed.dao.LongIdTable

object TrackedTwitterAccounts : LongIdTable() {
    val guildId = long("guild").index()
    val channelId = long("channel")
    val twitterAccountId = long("twitter_account_id").index()
    val message = text("message")
}