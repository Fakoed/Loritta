package net.perfectdreams.loritta.tables

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mrpowergamerbr.loritta.utils.exposed.array
import com.mrpowergamerbr.loritta.utils.exposed.rawJsonb
import net.perfectdreams.loritta.api.utils.Rarity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.TextColumnType

object Backgrounds : IdTable<String>() {
    val internalName = text("internal_name").primaryKey()
    override val id: Column<EntityID<String>> = internalName.entityId()

    val imageFile = text("image_file")
    val enabled = bool("enabled").index()
    val rarity = enumeration("rarity", Rarity::class).index()
    val createdBy = array<String>("created_by", TextColumnType())
    val crop = rawJsonb("crop", Gson(), JsonParser()).nullable()
    val availableToBuyViaDreams = bool("available_to_buy_via_dreams").index()
    val availableToBuyViaMoney = bool("available_to_buy_via_money").index()
    val set = optReference("set", Sets)
}