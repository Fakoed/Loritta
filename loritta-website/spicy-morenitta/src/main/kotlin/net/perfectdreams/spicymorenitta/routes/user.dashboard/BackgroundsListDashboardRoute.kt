package net.perfectdreams.spicymorenitta.routes.user.dashboard

import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.*
import kotlinx.serialization.json.JSON
import net.perfectdreams.loritta.api.utils.Rarity
import net.perfectdreams.spicymorenitta.SpicyMorenitta
import net.perfectdreams.spicymorenitta.application.ApplicationCall
import net.perfectdreams.spicymorenitta.http
import net.perfectdreams.spicymorenitta.locale
import net.perfectdreams.spicymorenitta.routes.UpdateNavbarSizePostRender
import net.perfectdreams.spicymorenitta.utils.*
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass
import kotlin.dom.clear
import kotlin.dom.hasClass
import kotlin.dom.removeClass
import kotlin.js.Date

class BackgroundsListDashboardRoute(val m: SpicyMorenitta) : UpdateNavbarSizePostRender("/user/@me/dashboard/backgrounds") {
    override val keepLoadingScreen: Boolean
        get() = true
    private val activeBackgroundTitleElement: Element
        get() = document.select("#active-background-title")
    private val activeBackgroundDescriptionElement: Element
        get() = document.select("#active-background-description")
    private val activeBackgroundSetElement: Element
        get() = document.select("#active-background-set")
    private val activateBackgroundButtonElement: HTMLButtonElement
        get() = document.select(".activate-button")
    private var activeBackground: AllBackgroundsListDashboardRoute.Background? = null
    private var enabledBackground: AllBackgroundsListDashboardRoute.Background? = null

    @UseExperimental(ImplicitReflectionSerializer::class)
    override fun onRender(call: ApplicationCall) {
        super.onRender(call)

        m.showLoadingScreen()

        SpicyMorenitta.INSTANCE.launch {
            fixDummyNavbarHeight(call)
            /* m.fixLeftSidebarScroll {
                switchContent(call)
            } */

            val userBackgroundsJob = m.async {
                debug("Retrieving profiles & background info...")
                val payload = http.get<String> {
                    url("${window.location.origin}/api/v1/users/@me/backgrounds,settings")
                }

                debug("Retrieved profiles & background info!")
                val result = kotlinx.serialization.json.JSON.nonstrict.parse<UserInfoResult>(payload)
                return@async result
            }

            // ===[ USER PROFILE IMAGE ]===
            val profileWrapperJob = m.async {
                val profileWrapper = Image()
                debug("Awaiting load...")
                profileWrapper.awaitLoad("${window.location.origin}/api/v1/users/@me/profile?t=${Date().getTime()}")
                debug("Load complete!")
                profileWrapper
            }

            val result = userBackgroundsJob.await()
            val profileWrapper = profileWrapperJob.await()

            val fanArtArtistsJob = m.async {
                val allArtists = result.backgrounds.mapNotNull { it.createdBy }.flatten().distinct()

                if (allArtists.isEmpty())
                    return@async listOf<FanArtArtist>()

                val payload = http.get<String> {
                    url("${window.location.origin}/api/v1/loritta/fan-arts?query=all&filter=${allArtists.joinToString(",")}")
                }

                JSON.nonstrict.parseList<FanArtArtist>(payload)
            }

            val fanArtArtists = fanArtArtistsJob.await()

            val entriesDiv = document.select<HTMLDivElement>("#bundles-content")

            val backgrounds = result.backgrounds.sortedByDescending { it.rarity.getBackgroundPrice() }
                    .toMutableList()
                    .apply {
                        this.add(
                                AllBackgroundsListDashboardRoute.Background(
                                        "random",
                                        "random.png",
                                        true,
                                        Rarity.COMMON,
                                        listOf(),
                                        null,
                                        null,
                                        null
                                )
                        )
                    }

            entriesDiv.append {
                div("loritta-items-list") {
                    div(classes = "loritta-items-wrapper") {
                        for (background in backgrounds) {
                            div(classes = "shop-item-entry rarity-${background.rarity.name.toLowerCase()}") {
                                div {
                                    style = "position: relative;"

                                    div {
                                        style = "overflow: hidden; line-height: 0;"

                                        canvas("canvas-background-preview") {
                                            id = "canvas-preview-${background.internalName}"
                                            width = "800"
                                            height = "600"
                                            style = "width: 100px; height: auto;"
                                        }
                                    }
                                }
                            }
                        }
                    }

                    div(classes = "loritta-items-sidebar") {
                        div(classes = "canvas-preview-wrapper") {
                            canvas("canvas-preview-only-bg") {
                                style = """width: 350px;"""
                                width = "800"
                                height = "600"
                            }

                            canvas("canvas-preview") {
                                style = """width: 350px;"""
                                width = "800"
                                height = "600"
                            }
                        }

                        h2 {
                            id = "active-background-title"
                            style = "word-break: break-word; text-align: center;"
                        }
                        div {
                            id = "active-background-description"
                            style = "margin-bottom: 10px;"
                        }

                        div {
                            id = "active-background-set"
                        }

                        div {
                            style = "text-align: center;"

                            button(classes = "activate-button button-discord button-discord-success pure-button") {
                                style = "font-size: 1.5em;"
                                + "Ativar"

                                onClickFunction = {
                                    if (!activateBackgroundButtonElement.hasClass("button-discord-disabled")) {
                                        activeBackground?.let { activeBackground ->
                                            SaveUtils.prepareSave("profile_design", endpoint = "${loriUrl}api/v1/users/self-profile", extras = {
                                                it["setActiveBackground"] = activeBackground.internalName
                                            }, onFinish = {
                                                if (it.statusCode in 200..299) {
                                                    activateBackgroundButtonElement.addClass("button-discord-disabled")
                                                    activateBackgroundButtonElement.removeClass("button-discord-success")

                                                    enabledBackground = activeBackground
                                                }
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (background in backgrounds) {
                    val canvasPreview = document.select<HTMLCanvasElement>("#canvas-preview-${background.internalName}")

                    m.launch {
                        val (image) = LockerUtils.prepareBackgroundCanvasPreview(m, background, canvasPreview)

                        canvasPreview.parentElement!!.parentElement!!.onClick {
                            updateActiveBackground(profileWrapper, background, image, fanArtArtists)
                        }

                        if (background.internalName == (result.settings.activeBackground ?: "defaultBlue")) {
                            enabledBackground = background
                            updateActiveBackground(profileWrapper, background, image, fanArtArtists)
                        }
                    }
                }
            }
            m.hideLoadingScreen()
        }
    }

    fun updateActiveBackground(profileWrapper: Image, background: AllBackgroundsListDashboardRoute.Background, backgroundImg: Image, fanArtArtists: List<FanArtArtist>) {
        this.activeBackground = background

        if (enabledBackground == background) {
            activateBackgroundButtonElement.addClass("button-discord-disabled")
            activateBackgroundButtonElement.removeClass("button-discord-success")
        } else {
            activateBackgroundButtonElement.removeClass("button-discord-disabled")
            activateBackgroundButtonElement.addClass("button-discord-success")
        }

        val canvasCheckout = document.select<HTMLCanvasElement>(".canvas-preview")
        val canvasCheckoutOnlyBg = document.select<HTMLCanvasElement>(".canvas-preview-only-bg")

        val canvasPreviewContext = (canvasCheckout.getContext("2d")!! as CanvasRenderingContext2D)
        val canvasPreviewOnlyBgContext = (canvasCheckoutOnlyBg.getContext("2d")!! as CanvasRenderingContext2D)

        canvasPreviewContext
                .drawImage(
                        backgroundImg,
                        (background.crop?.offsetX ?: 0).toDouble(),
                        (background.crop?.offsetY ?: 0).toDouble(),
                        (background.crop?.width ?: backgroundImg.width).toDouble(),
                        (background.crop?.height ?: backgroundImg.height).toDouble(),
                        0.0,
                        0.0,
                        800.0,
                        600.0
                )
        canvasPreviewOnlyBgContext
                .drawImage(
                        backgroundImg,
                        (background.crop?.offsetX ?: 0).toDouble(),
                        (background.crop?.offsetY ?: 0).toDouble(),
                        (background.crop?.width ?: backgroundImg.width).toDouble(),
                        (background.crop?.height ?: backgroundImg.height).toDouble(),
                        0.0,
                        0.0,
                        800.0,
                        600.0
                )

        canvasPreviewContext.drawImage(profileWrapper, 0.0, 0.0)

        activeBackgroundTitleElement.textContent = locale["backgrounds.${background.internalName}.title"]
        activeBackgroundDescriptionElement.textContent = locale["backgrounds.${background.internalName}.description"]

        if (background.createdBy != null) {
            val artists = fanArtArtists.filter { it.id in background.createdBy }
            if (artists.isNotEmpty()) {
                activeBackgroundDescriptionElement.append {
                    artists.forEach {
                        div {
                            val name = (it.info.override?.name ?: it.user?.name ?: it.info.name ?: it.id)

                            +"Criado por "
                            a(href = "/fanarts/${it.id}") {
                                +name
                            }
                        }
                    }
                }
            }
        }

        activeBackgroundSetElement.clear()
        if (background.set != null) {
            activeBackgroundSetElement.let {
                it.clear()
                it.append {
                    i {
                        + "Parte do conjunto "

                        b {
                            +(locale["sets.${background.set}"])
                        }
                    }
                }
            }
        }
    }

    @Serializable
    class UserInfoResult(
            val settings: Settings,
            var backgrounds: MutableList<AllBackgroundsListDashboardRoute.Background>
    )

    @Serializable
    class Settings(
            @Optional val activeBackground: String? = null,
            @Optional val activeProfileDesign: String? = null
    )
}