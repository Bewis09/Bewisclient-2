package bewis09.bewisclient.autoUpdate

import bewis09.bewisclient.settingsLoader.SettingsLoader
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import java.net.URL
import java.util.*

object UpdateChecker {
    fun checkForUpdates(): JsonObject? {
        try {
            val scanner = Scanner(URL("https://api.modrinth.com/v2/project/bewisclient/version").openStream())
            val response = scanner.nextLine()
            scanner.close()

            val jsonResponse = Gson().fromJson(response, JsonArray::class.java)

            var new: JsonObject? = null
            var vListed = false
            var versionNumber = 0

            for (r in jsonResponse) {
                if(r.isJsonObject) {
                    val l = r.asJsonObject

                    if(l.get("version_number").asString== getCurrentVersion()) {
                        vListed = true
                        break
                    }

                    if(l.get("game_versions").asJsonArray.contains(JsonPrimitive(SharedConstants.getGameVersion().name))) {
                        if(getVersionNumber(l.get("version_type").asString)>=getCurrentVersionNumber() && (new==null || getVersionNumber(l.get("version_type").asString)>versionNumber)) {
                            new = l
                            versionNumber = getVersionNumber(l.get("version_type").asString)
                        }
                    }
                }
            }

            if(vListed)
                return new
        } catch (_: Exception) {}

        return null
    }

    fun getCurrentVersion(): String {
        val mod = FabricLoader.getInstance().getModContainer("bewisclient")

        if(mod.isPresent) {
            val m = mod.get()
            val v = m.metadata.version.friendlyString

            return v
        }

        return ""
    }

    fun getCurrentVersionNumber(): Int {
        val v = getCurrentVersion()
        if(v.split("-").size<2) return 2
        return getVersionNumber(v.split("-")[1].split(".")[0])
    }

    fun getVersionNumber(s: String): Int {
        when (s) {
            "beta"->return 1
            "alpha"->return 0
        }
        return 2
    }
}