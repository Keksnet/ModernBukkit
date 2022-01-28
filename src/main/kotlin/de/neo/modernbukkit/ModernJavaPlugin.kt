package de.neo.modernbukkit

import de.neo.modernbukkit.command.CommandManager
import de.neo.modernbukkit.util.Universal
import org.bukkit.plugin.java.JavaPlugin

abstract class ModernJavaPlugin : JavaPlugin() {

    private val commandManager = CommandManager()
    private val messages = HashMap<String, String>()
    private val config = HashMap<String, Universal>()

    override fun onEnable() {
        initMessages()
        onStart()
    }

    private fun initMessages() {
        messages["no_permission"] = "§cYou do not have the permission \$permission"
    }

    abstract fun onStart()

    fun registerCommands(commandClass: Any) {
        commandManager.registerCommands(this, commandClass)
    }

    fun addMessage(key: String, value: String) {
        messages[key] = value
    }

    fun getMessage(key: String) : String = messages[key] ?: ""

    fun setConfigEntry(key: String, value: Any) = setConfigEntry(key, Universal(value))

    fun setConfigEntry(key: String, value: Universal) {
        config[key] = value
    }

    fun getConfigEntry(key: String) = config[key] ?: Universal(false)

}
