package net.perfectdreams.loritta.website.routes.dashboard.configure

import com.mrpowergamerbr.loritta.Loritta
import com.mrpowergamerbr.loritta.userdata.PermissionsConfig
import com.mrpowergamerbr.loritta.utils.LorittaPermission
import com.mrpowergamerbr.loritta.utils.locale.BaseLocale
import com.mrpowergamerbr.loritta.website.evaluate
import io.ktor.application.ApplicationCall
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.perfectdreams.loritta.platform.discord.LorittaDiscord
import net.perfectdreams.loritta.website.routes.dashboard.RequiresGuildAuthLocalizedRoute
import net.perfectdreams.loritta.website.session.LorittaJsonWebSession
import net.perfectdreams.loritta.website.utils.extensions.legacyVariables
import net.perfectdreams.loritta.website.utils.extensions.respondHtml
import net.perfectdreams.temmiediscordauth.TemmieDiscordAuth
import kotlin.collections.set

class ConfigurePermissionsRoute(loritta: LorittaDiscord) : RequiresGuildAuthLocalizedRoute(loritta, "/configure/permissions") {
	override suspend fun onGuildAuthenticatedRequest(call: ApplicationCall, locale: BaseLocale, discordAuth: TemmieDiscordAuth, userIdentification: LorittaJsonWebSession.UserIdentification, guild: Guild) {
		loritta as Loritta
		val serverConfig = loritta.getServerConfigForGuild(guild.id)

		val variables = call.legacyVariables(locale)

		variables["saveType"] = "permissions"
		val roleConfig = mutableMapOf<Role, MutableMap<String, Boolean>>()

		for (role in guild.roles) {
			val roleConf = serverConfig.permissionsConfig.roles.getOrDefault(role.id, PermissionsConfig.PermissionRole())
			val permissionMap = mutableMapOf<String, Boolean>()

			for (permission in LorittaPermission.values()) {
				permissionMap[permission.internalName] = roleConf.permissions.contains(permission)
			}

			roleConfig[role] = permissionMap
		}

		variables["roleConfigs"] = roleConfig

		call.respondHtml(evaluate("permissions.html", variables))
	}
}