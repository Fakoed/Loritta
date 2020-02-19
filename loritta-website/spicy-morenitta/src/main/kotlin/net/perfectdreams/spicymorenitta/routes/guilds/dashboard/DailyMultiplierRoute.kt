package net.perfectdreams.spicymorenitta.routes.guilds.dashboard

import LoriDashboard
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import net.perfectdreams.spicymorenitta.SpicyMorenitta
import net.perfectdreams.spicymorenitta.application.ApplicationCall
import net.perfectdreams.spicymorenitta.routes.UpdateNavbarSizePostRender
import net.perfectdreams.spicymorenitta.utils.*
import net.perfectdreams.spicymorenitta.utils.DashboardUtils.launchWithLoadingScreenAndFixContent
import net.perfectdreams.spicymorenitta.utils.DashboardUtils.switchContentAndFixLeftSidebarScroll
import net.perfectdreams.spicymorenitta.views.dashboard.ServerConfig
import net.perfectdreams.spicymorenitta.views.dashboard.Stuff
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

class DailyMultiplierRoute(val m: SpicyMorenitta) : UpdateNavbarSizePostRender("/guild/{guildid}/configure/daily-multiplier") {
	override val keepLoadingScreen: Boolean
		get() = true

	@Serializable
	class PartialGuildConfiguration(
			val activeDonationKeys: List<ServerConfig.DonationKey>,
			val donationConfig: ServerConfig.DonationConfig
	)

	@ImplicitReflectionSerializer
	override fun onRender(call: ApplicationCall) {
		launchWithLoadingScreenAndFixContent(call) {
			val guild = DashboardUtils.retrievePartialGuildConfiguration<PartialGuildConfiguration>(call.parameters["guildid"]!!, "donation", "activekeys")
			switchContentAndFixLeftSidebarScroll(call)

			document.select<HTMLDivElement>("#save-button").onClick {
				prepareSave()
			}

			(page.getElementById("cmn-toggle-1") as HTMLInputElement).checked = guild.donationConfig.dailyMultiplier

			LoriDashboard.applyBlur("#hiddenIfDisabled", "#cmn-toggle-1") {
				val donationValue = guild.activeDonationKeys.sumByDouble { it.value }

				if (59.99 > donationValue) {
					Stuff.showPremiumFeatureModal()
					return@applyBlur false
				}
				return@applyBlur true
			}
		}
	}

	fun prepareSave() {
		SaveUtils.prepareSave("donation", extras = {
			it["dailyMultiplier"] = (page.getElementById("cmn-toggle-1") as HTMLInputElement).checked
		})
	}
}