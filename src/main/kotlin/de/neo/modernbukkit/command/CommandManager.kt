package de.neo.modernbukkit.command

import de.neo.modernbukkit.ModernJavaPlugin
import de.neo.modernbukkit.command.annotation.Command
import de.neo.modernbukkit.command.annotation.Subcommand
import de.neo.modernbukkit.command.annotation.TabCompletion
import de.neo.modernbukkit.util.ConfigConstants
import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabCompleter
import java.lang.reflect.Method

const val SHORT_ARGS_COUNT = 2
const val NORMAL_BUKKIT_ARGS_COUNT = 4

class CommandManager {

    private val registeredCommands = HashMap<String, Pair<String, Method>>()
    private val pluginCommands = HashMap<String, PluginCommand>()

    fun registerCommands(plugin: ModernJavaPlugin, commandClass: Any) {
        for(method in commandClass.javaClass.methods) {
            if(method.getAnnotation(Command::class.java) != null) {
                handleCommandAnnotation(method, plugin, commandClass)
            }else if(method.getAnnotation(Subcommand::class.java) != null) {
                handleSubcommandAnnotation(method)
            }else if(method.getAnnotation(de.neo.modernbukkit.command.annotation.TabCompleter::class.java) != null) {
                handleTabCompleterAnnotation(method, commandClass)
            }else {
                plugin.logger.info("Method ${method.name} has no annotation")
            }
        }
    }

    private fun handleCommandAnnotation(method: Method, plugin: ModernJavaPlugin, commandClass: Any) {
        val command = method.getAnnotation(Command::class.java)
        val completionFormat = if(method.getAnnotation(TabCompletion::class.java) != null) {
            method.getAnnotation(TabCompletion::class.java).format
        }else {
            ""
        }
        registeredCommands[command.command] = Pair(completionFormat, method)
        val plCommand = plugin.getCommand(command.command)
        setCommandExecutor(plCommand, command, plugin, completionFormat, method, commandClass)
        if(plCommand?.tabCompleter == null) {
            setDefaultTabCompleter(plugin, plCommand, command, completionFormat)
        }
    }

    private fun setCommandExecutor(plCommand: PluginCommand?, command: Command, plugin: ModernJavaPlugin,
                                   completionFormat: String, method: Method, commandClass: Any) {
        plCommand?.setExecutor { sender, cmd, label, args ->
            val subcommand = if(args.isNotEmpty()) {
                args[0]
            }else {
                ""
            }
            val handler = (registeredCommands["${command.command}/$subcommand"]
                ?: Pair(completionFormat, method)).second
            if(handler == method) {
                val permission = handler.getAnnotation(Command::class.java).permission
                if(sender.hasPermission(permission)) {
                    if (handler.parameterCount == SHORT_ARGS_COUNT) {
                        handler.invoke(commandClass, sender, args)
                    } else if(handler.parameterCount == NORMAL_BUKKIT_ARGS_COUNT) {
                        handler.invoke(commandClass, sender, cmd, label, args)
                    }
                }else {
                    sender.sendMessage(plugin.getMessage("no_permission")
                        .replace("\$permission", permission))
                }
            }else {
                val permission = handler.getAnnotation(Subcommand::class.java).permission
                if(sender.hasPermission(permission)) {
                    val subcommandArgs = Array<String>(args.size - 1) { args[it + 1] }
                    if (handler.parameterCount == SHORT_ARGS_COUNT) {
                        handler.invoke(commandClass, sender, subcommandArgs)
                    } else if(handler.parameterCount == NORMAL_BUKKIT_ARGS_COUNT) {
                        handler.invoke(commandClass, sender, cmd, label, subcommandArgs)
                    }
                }else {
                    sender.sendMessage(plugin.getMessage("no_permission")
                        .replace("\$permission", permission))
                }
            }
            false
        }
    }

    private fun setDefaultTabCompleter(plugin: ModernJavaPlugin, plCommand: PluginCommand?, command: Command, completionFormat: String) {
        plCommand?.setTabCompleter { sender, _, _, args ->
            val completions = ArrayList<String>()
            registeredCommands.filter {
                it.key.startsWith("${command.command}/")
                        && it.value.second.getAnnotation(Subcommand::class.java) != null
                        && it.value.second.getAnnotation(Subcommand::class.java).position == args.size
                        && (plugin.getConfigEntry(ConfigConstants.HIDE_UNPERMISSIONED_SUBCOMMANDS).asBool()
                            && sender.hasPermission(it.value.second.getAnnotation(Subcommand::class.java).permission))
            }.forEach {
                completions.add(it.key.replace("${command.command}/", ""))
            }
            if(completions.isEmpty()) {
                val completionPart = if(completionFormat.split(" ").size > args.size - 1) {
                    completionFormat.split(" ")[args.size - 1]
                }else {
                    ""
                }
                when(completionPart) {
                    "\$players" -> Bukkit.getOnlinePlayers().forEach { completions.add(it.name) }

                    else -> completions.add(completionPart)
                }
            }
            return@setTabCompleter completions
        }
    }

    private fun handleSubcommandAnnotation(method: Method) {
        val subcommand = method.getAnnotation(Subcommand::class.java)
        val completionFormat = if(method.getAnnotation(TabCompletion::class.java) != null) {
            method.getAnnotation(TabCompletion::class.java).format
        }else {
            ""
        }
        registeredCommands["${subcommand.rootCommand}/${subcommand.subCommand}"] =
            Pair(completionFormat, method)
    }

    private fun handleTabCompleterAnnotation(method: Method, commandClass: Any) {
        val tabCompleter = method.getAnnotation(de.neo.modernbukkit.command.annotation.TabCompleter::class.java)
        pluginCommands[tabCompleter.command]?.tabCompleter =
            TabCompleter { sender, cmd, alias, args ->
                if (method.returnType is List<*>) {
                    if (method.parameterCount == SHORT_ARGS_COUNT) {
                        return@TabCompleter method.invoke(commandClass, sender, args) as List<String>
                    } else if (method.parameterCount == NORMAL_BUKKIT_ARGS_COUNT) {
                        return@TabCompleter method.invoke(commandClass, sender, cmd, alias, args)
                                as List<String>
                    }
                }
                return@TabCompleter listOf<String>()
            }
    }
}
