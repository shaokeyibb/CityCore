package kim.minecraft.citycore.features

import io.izzel.taboolib.module.inject.TListener
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.vehicle.VehicleExitEvent

@Suppress("unused")
@TListener
object MultiPassengerVehicles:Listener {
    @EventHandler
    fun joinVehicle(e: PlayerInteractEntityEvent) {
        val rightClicked = e.rightClicked
        if (rightClicked.isInsideVehicle) {
            val passengers = rightClicked.passengers
            if (passengers.isEmpty()) {
                rightClicked.addPassenger(e.player)
            } else {
                passengers.iterator().forEachRemaining {
                    if (it != e.player && it.passengers.isEmpty()) {
                        it.addPassenger(e.player)
                    }
                }
            }
        }
    }

    @EventHandler
    fun leaveVehicle(e: VehicleExitEvent) {
        var entityPastBy: Entity = e.exited
        e.exited.passengers.iterator().forEachRemaining {
            entityPastBy.removePassenger(it)
            entityPastBy = it
        }
    }

    @EventHandler
    fun onShift(e: PlayerToggleSneakEvent) {
        var entityPastBy: Entity = e.player
        e.player.passengers.iterator().forEachRemaining {
            entityPastBy.removePassenger(it)
            entityPastBy = it
        }
    }
}