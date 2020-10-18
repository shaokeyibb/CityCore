package kim.minecraft.citycore.utils.nbt

import io.izzel.taboolib.module.nms.NMS
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.inventory.ItemStack

object NBTHelper {

    private fun getNBTCompound(itemStack: ItemStack): NBTCompound {
        return NMS.handle().loadNBT(itemStack)
    }

    fun ItemStack.getNBT(key: String): String? {
        return getNBTCompound(this)["CityCore_$key"]?.asString()
    }

    fun ItemStack.addNBT(key: String, value: String) {
        getNBTCompound(this).also {
            it["CityCore_$key"] = NBTBase(value)
            it.saveTo(this)
        }
    }

    fun ItemStack.removeNBT(key: String) {
        getNBTCompound(this).also {
            it.remove(key)
            it.saveTo(this)
        }
    }

}