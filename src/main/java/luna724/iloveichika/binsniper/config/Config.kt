package luna724.iloveichika.binsniper.config

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import gg.skytils.ktor.websocket.DefaultWebSocketSession
import io.github.notenoughupdates.moulconfig.observer.PropertyTypeAdapterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.getPlayerId
import java.io.File
import java.util.UUID
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

@Serializable
data class SessionConfig(
    var Cost: Int = -1,
    var Mode: String = "FASTMODE",
    var Delay: Long = 1000L,
    var Reconnect: Boolean = true,
    var Name: String = "None",
    var Category: Int = 0,
    var Amount: Int = 0,
    var Timeout: Long = 30000L,
    var Message: Boolean = true,
    var UUIDMode: Boolean = true,
    var SleepOptimization: Boolean = false,
    var Active: Boolean = false,
    var Debug: Boolean = false,
    // TODO
    var TinyDynamicRest: Boolean = true,
    var AntiAntiMacro: Boolean = true,
    var BackCompatibility: Boolean = false,
)

class Config {
    companion object {
        val gson = GsonBuilder().setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeSpecialFloatingPointValues()
            .registerTypeAdapterFactory(PropertyTypeAdapterFactory())
            .registerTypeAdapter(UUID::class.java, object : TypeAdapter<UUID>() {
                override fun write(out: JsonWriter, value: UUID) {
                    out.value(value.toString())
                }

                override fun read(reader: JsonReader): UUID {
                    return UUID.fromString(reader.nextString())
                }
            }.nullSafe())
            .enableComplexMapKeySerialization()
            .create()

        val configDirectory = File("config/BinSniper")
        val configFile = File(configDirectory, "SessionObjects.json")
    }

    init {
        configDirectory.mkdirs()
        if (!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText("{}")
        }
    }

    fun loadAll(): LinkedHashMap<String, SessionConfig> {
        if (!configFile.exists()) {
            ChatLib.chat("設定ファイルが見つかりませんでした。/bs cleanで設定を作成してください")
            return LinkedHashMap()
        }
        val mapSerializer = MapSerializer(String.serializer(), SessionConfig.serializer())

        val jsonRaw = configFile.readText()
        return LinkedHashMap(Json.decodeFromString(mapSerializer, jsonRaw))
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveAll(obj: LinkedHashMap<String, SessionConfig>) {
        if (!configFile.exists()) {
            configFile.createNewFile()
        }
        val jsonFormatter = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }

        val mapSerializer = MapSerializer(String.serializer(), SessionConfig.serializer())
        val jsonString = jsonFormatter.encodeToString(mapSerializer, obj)
        configFile.writeText(jsonString)
    }

    fun savePlayer(
        sessionConfig: SessionConfig,
        key: String? = null
    ) {
        val mapKey: String = key ?: getPlayerId()
        val allObj = loadAll()
        allObj[mapKey] = sessionConfig

        saveAll(allObj)
    }

    fun loadPlayer(key: String? = null): SessionConfig {
        val allObj = loadAll()
        val id: String = key ?: getPlayerId()

        if (id in allObj.keys) {
            return allObj[id]!!
        }
        return SessionConfig()
    }
}