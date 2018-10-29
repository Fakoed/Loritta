package com.mrpowergamerbr.loritta.utils.locale

import com.mrpowergamerbr.loritta.utils.f
import mu.KotlinLogging

/**
 * Localization class, this is partly generated by the LocaleGenerator
 */
open class BaseLocale {
	companion object {
		@JvmStatic
		private val logger = KotlinLogging.logger {}

		private val DEFAULT_MESSAGE = LocaleMessage("Whoopsie! Missing translation string here! Fix it!!!")
		private val DEFAULT_LIST = listOf(LocaleMessage("Whoopsie! Missing transation list here! Fix it!!!"))
	}

	@Transient
	@Deprecated("Please use the inner classes")
	var strings = mutableMapOf<String, String>()
	@Transient
	var commands = Commands()
	@Transient
	var loritta = Loritta()

	@Deprecated("Please use the inner classes")
	operator fun get(key: String, vararg arguments: Any?): String {
		if (!strings.containsKey(key)) {
			logger.warn {"Missing translation key! $key" }
			return key
		}
		return strings[key]!!.f(*arguments)
	}

	// Generic
	lateinit var SHIP_valor90: List<String>

	lateinit var SHIP_valor80: List<String>

	lateinit var SHIP_valor70: List<String>

	lateinit var SHIP_valor60: List<String>

	lateinit var SHIP_valor50: List<String>

	lateinit var SHIP_valor40: List<String>

	lateinit var SHIP_valor30: List<String>

	lateinit var SHIP_valor20: List<String>

	lateinit var SHIP_valor10: List<String>

	lateinit var SHIP_valor0: List<String>

	class Loritta {
		var translationAuthors: List<LocaleMessage> = DEFAULT_LIST
	}
	class Commands {
		var pleaseWaitCooldown: LocaleMessage = DEFAULT_MESSAGE
		var errorWhileExecutingCommand: LocaleMessage = DEFAULT_MESSAGE
		var cantUseInPrivate: LocaleMessage = DEFAULT_MESSAGE
		var userDoesNotExists: LocaleMessage = DEFAULT_MESSAGE
		class Arguments {
			var text: LocaleMessage = DEFAULT_MESSAGE
			var number: LocaleMessage = DEFAULT_MESSAGE
			var user: LocaleMessage = DEFAULT_MESSAGE
			var image: LocaleMessage = DEFAULT_MESSAGE
		}
		var arguments = Arguments()

		class Ajuda {
			var errorWhileOpeningDm: LocaleMessage = DEFAULT_MESSAGE
		}
		var ajuda = Ajuda()

		class Roll {
			var description: LocaleMessage = DEFAULT_MESSAGE
			var howMuchSides: LocaleMessage = DEFAULT_MESSAGE
		}
		var roll = Roll()

		class Vieirinha {
			var description: LocaleMessage = DEFAULT_MESSAGE
			var examples: List<LocaleMessage> = DEFAULT_LIST
			var responses: List<LocaleMessage> = DEFAULT_LIST
		}
		var vieirinha = Vieirinha()

		class Actions {
			var examples: List<LocaleMessage> = DEFAULT_LIST
			class Slap {
				var description: LocaleMessage = DEFAULT_MESSAGE
				var response: LocaleMessage = DEFAULT_MESSAGE
				var responseAntiIdiot: LocaleMessage = DEFAULT_MESSAGE
			}
			var slap = Slap()

			class Kiss {
				var description: LocaleMessage = DEFAULT_MESSAGE
				var response: LocaleMessage = DEFAULT_MESSAGE
				var responseAntiIdiot: LocaleMessage = DEFAULT_MESSAGE
			}
			var kiss = Kiss()

			class Attack {
				var description: LocaleMessage = DEFAULT_MESSAGE
				var response: LocaleMessage = DEFAULT_MESSAGE
				var responseAntiIdiot: LocaleMessage = DEFAULT_MESSAGE
			}
			var attack = Attack()

			class Hug {
				var description: LocaleMessage = DEFAULT_MESSAGE
				var response: LocaleMessage = DEFAULT_MESSAGE
			}
			var hug = Hug()

			class Dance {
				var description: LocaleMessage = DEFAULT_MESSAGE
				var response: LocaleMessage = DEFAULT_MESSAGE
			}
			var dance = Dance()

		}
		var actions = Actions()
	}
}