package net.perfectdreams.loritta.plugin.parallaxroutes.routes

import com.github.salomonbrys.kotson.jsonObject
import com.mrpowergamerbr.loritta.utils.extensions.await
import com.mrpowergamerbr.loritta.utils.lorittaShards
import io.ktor.application.ApplicationCall
import net.perfectdreams.loritta.platform.discord.LorittaDiscord
import net.perfectdreams.loritta.website.routes.api.v1.RequiresAPIAuthenticationRoute
import net.perfectdreams.loritta.website.utils.extensions.respondJson

class DeleteRoleFromMemberRoute(loritta: LorittaDiscord) : RequiresAPIAuthenticationRoute(loritta, "/api/v1/parallax/guilds/{guildId}/members/{memberId}/roles/{roleId}") {
	override suspend fun onAuthenticatedRequest(call: ApplicationCall) {
		try {
			val guildId = call.parameters["guildId"]!!
			val memberId = call.parameters["memberId"]!!
			val roleId = call.parameters["roleId"]!!

			val guild = lorittaShards.getGuildById(guildId)!!
			val member = guild.getMemberById(memberId)!!
			val role = guild.getRoleById(roleId)!!

			if (guild.selfMember.canInteract(role)) {
				guild.removeRoleFromMember(member, role).await()

				call.respondJson(jsonObject())
			}
		} catch (e: Throwable) {
			e.printStackTrace()
		}
	}
}