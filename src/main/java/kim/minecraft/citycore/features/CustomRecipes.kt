package kim.minecraft.citycore.features

import kim.minecraft.citycore.CityCore
import kim.minecraft.citycore.utils.nbt.NBTHelper.addNBT
import kim.minecraft.citycore.utils.nbt.NBTHelper.getNBT
import kim.minecraft.citycore.utils.storage.SettingsStorage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

object CustomRecipes {
    fun poisonFood() {
        Material.values().filter { it.isEdible }.forEach { material ->
            ShapelessRecipe(NamespacedKey(CityCore.plugin, "potion_${material.name}"),
                    ItemStack(material).also {
                        it.addNBT("hasPoison", true.toString())
                    })
                    .addIngredient(material)
                    .addIngredient(Material.SPIDER_EYE)
                    .also { shapelessRecipe -> Bukkit.addRecipe(shapelessRecipe) }
        }
        Bukkit.getPluginManager().registerEvents(object : Listener {
            @EventHandler
            fun onEat(e: PlayerItemConsumeEvent) {
                if (e.item.type.isEdible && e.item.getNBT("hasPoison").toBoolean()) {
                    object : BukkitRunnable() {
                        override fun run() {
                            e.player.health = 0.0
                        }
                    }.runTaskLater(CityCore.plugin, Random.nextLong(
                            SettingsStorage.settings.getLong("FoodPoisonTicksFrom", 72000),
                            SettingsStorage.settings.getLong("FoodPoisonTicksUntil", 144000)))
                }
            }
        }, CityCore.plugin)
    }

    init {
        poisonFood()
    }
}

