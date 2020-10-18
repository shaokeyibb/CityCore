package kim.minecraft.citycore.permission

interface PermissionHolder {

    val permissions: MutableList<String>

    fun hasPermission(permission: String): Boolean = permissions.contains(permission)

    fun addPermission(permission: String): Boolean {
        if (hasPermission(permission)) return false
        permissions.add(permission)
        return true
    }

    fun removePermission(permission: String): Boolean {
        if (!hasPermission(permission)) return false
        permissions.remove(permission)
        return true
    }

}