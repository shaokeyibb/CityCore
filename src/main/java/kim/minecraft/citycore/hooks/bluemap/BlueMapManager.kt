package kim.minecraft.citycore.hooks.bluemap

import com.flowpowered.math.vector.Vector2d
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.marker.MarkerAPI
import de.bluecolored.bluemap.api.marker.Shape
import de.bluecolored.bluemap.api.marker.ShapeMarker
import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.chunk.ChunkManager
import kim.minecraft.citycore.politics.country.Country
import kim.minecraft.citycore.politics.country.CountryManager
import org.bukkit.scheduler.BukkitRunnable
import java.awt.Color
import kotlin.random.Random

object BlueMapManager {

    private fun marker(api: BlueMapAPI, marker: MarkerAPI, id: String, shape: Shape): ShapeMarker {
        return marker.createMarkerSet("CityCore")!!.createShapeMarker(id, api.getMap("world").get(), shape, 64.0F)
    }

    fun refreshBlueMap(api: BlueMapAPI, marker: MarkerAPI) {
        val colors: MutableMap<Country, Pair<Color, Color>> = mutableMapOf()
        CountryManager.countries.values.forEach {
            colors[it] = Pair(Color(102, 204, 255),
                    Color(Random.nextDouble(0.0, 1.0).toFloat(), Random.nextDouble(0.0, 1.0).toFloat(), Random.nextDouble(0.0, 1.0).toFloat(), 0.5F))
        }
        ChunkManager.chunks.forEach { entry ->
            marker(api, marker,
                    entry.key.toString(),
                    Shape(entry.value.getBukkitChunk().getBlock(0, 64, 0).run { Vector2d(location.x, location.z) },
                            entry.value.getBukkitChunk().getBlock(0, 64, 15).run { Vector2d(location.x, location.z) },
                            entry.value.getBukkitChunk().getBlock(15, 64, 15).run { Vector2d(location.x, location.z) },
                            entry.value.getBukkitChunk().getBlock(15, 64, 0).run { Vector2d(location.x, location.z) })).apply {
                if (entry.value.getBelongingsCountry() == null) {
                    setColors(Color.GRAY, Color(0.0F, 0.0F, 0.0F, 0.0F))
                } else {
                    colors[entry.value.getBelongingsCountry()]!!.also {
                        borderColor = it.first
                        fillColor = it.second
                    }
                }
                this.label = """
                    地块: ${entry.key}
                    <br>
                    所有者: ${entry.value.getBelongingsCountry()?.name ?: "无"}
                """.trimIndent()
            }
        }
        marker.save()
    }

    fun runRefresher() {
        BlueMapAPI.onEnable {
            CityCore.plugin.logger.info("加载 BlueMap 区块标记")
            object : BukkitRunnable() {
                override fun run() {
                    refreshBlueMap(it, it.markerAPI)
                }
            }.runTaskTimerAsynchronously(CityCore.plugin, 0, 3000)
        }
    }
}