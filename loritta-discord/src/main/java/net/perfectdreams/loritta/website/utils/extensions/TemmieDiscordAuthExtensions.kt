package net.perfectdreams.loritta.website.utils.extensions

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.mrpowergamerbr.loritta.utils.gson
import net.perfectdreams.temmiediscordauth.TemmieDiscordAuth

fun TemmieDiscordAuth.toJson(): String {
	return gson.toJson(
			jsonObject(
					"authCode" to this.authCode,
					"redirectUri" to this.redirectUri,
					"scope" to this.scope.toJsonArray(),
					"accessToken" to this.accessToken,
					"refreshToken" to this.refreshToken,
					"expiresIn" to this.expiresIn,
					"generatedAt" to this.generatedAt
			)
	)
}