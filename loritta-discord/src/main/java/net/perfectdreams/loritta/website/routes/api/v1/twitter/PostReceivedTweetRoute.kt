package net.perfectdreams.loritta.website.routes.api.v1.twitter

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.long
import com.github.salomonbrys.kotson.string
import com.mrpowergamerbr.loritta.network.Databases
import com.mrpowergamerbr.loritta.utils.MessageUtils
import com.mrpowergamerbr.loritta.utils.jsonParser
import com.mrpowergamerbr.loritta.utils.lorittaShards
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import mu.KotlinLogging
import net.perfectdreams.loritta.platform.discord.LorittaDiscord
import net.perfectdreams.loritta.tables.TrackedTwitterAccounts
import net.perfectdreams.loritta.website.routes.api.v1.RequiresAPIAuthenticationRoute
import net.perfectdreams.loritta.website.utils.extensions.respondJson
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class PostReceivedTweetRoute(loritta: LorittaDiscord) : RequiresAPIAuthenticationRoute(loritta, "/api/v1/twitter/users/show") {
	companion object {
		private val logger = KotlinLogging.logger {}
	}

	override suspend fun onAuthenticatedRequest(call: ApplicationCall) {
		val json = jsonParser.parse(call.receiveText())
		val tweetId = json["tweetId"].long
		val userId = json["userId"].long
		val screenName = json["screenName"].string

		logger.info { "Received status $tweetId from $screenName (${tweetId}), relayed from the master cluster!" }
		val configsTrackingAccount = transaction(Databases.loritta) {
			TrackedTwitterAccounts.select {
				TrackedTwitterAccounts.twitterAccountId eq userId
			}.toMutableList()
		}
		logger.info { "There are ${configsTrackingAccount.size} tracked configs tracking ${screenName} (${userId})" }

		for (tracked in configsTrackingAccount) {
			val guild = lorittaShards.getGuildById(tracked[TrackedTwitterAccounts.guildId]) ?: continue
			val textChannel = guild.getTextChannelById(tracked[TrackedTwitterAccounts.channelId]) ?: continue

			val message = MessageUtils.generateMessage(
					tracked[TrackedTwitterAccounts.message],
					listOf(),
					guild,
					mapOf(
							"link" to "https://twitter.com/${screenName}/status/${tweetId}"
					)
			) ?: continue

			textChannel.sendMessage(message).queue()
		}

		call.respondJson(jsonObject())
	}
}