package de.neo.modernbukkit

import de.neo.modernbukkit.command.CommandManager
import org.bukkit.plugin.java.JavaPlugin

abstract class ModernJavaPlugin : JavaPlugin() {

    private val commandManager = CommandManager()
    private val messages = HashMap<String, String>()

    override fun onEnable() {
        initMessages()
        onStart()
    }

    fun initMessages() {
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

}