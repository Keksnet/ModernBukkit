package de.neo.modernbukkit.command

import de.neo.modernbukkit.ModernJavaPlugin
import de.neo.modernbukkit.command.annotation.*
import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabCompleter
import java.lang.reflect.Method

class CommandManager {

    private val registeredCommands = HashMap<String, Pair<String, Method>>()
    private val pluginCommands = HashMap<String, PluginCommand>()

    fun registerCommands(plugin: ModernJavaPlugin, commandClass: Any) {
        for(method in commandClass.javaClass.methods) {
            if(method.getAnnotation(Command::class.java) != null) {
                val command = method.getAnnotation(Command::class.java)
                val completionFormat = if(method.getAnnotation(TabCompletion::class.java) != null) {
                    method.getAnnotation(TabCompletion::class.java).format
                }else {
                    ""
                }
                registeredCommands[command.command] = Pair(completionFormat, method)
                val plCommand = plugin.getCommand(command.command)
                plCommand?.setExecutor { sender, cmd, label, args ->
                    val subcommand = if(args.isNotEmpty()) {
                        args[0]
                    }else {
                        ""
                    }
                    val handler = (registeredCommands["${command.command}/$subcommand"] ?: Pair(completionFormat, method)).second
                    if(handler == method) {
                        val permission = handler.getAnnotation(Command::class.java).permission
                        if(sender.hasPermission(permission)) {
                            if (handler.parameterCount == 2) {
                                handler.invoke(commandClass, sender, args)
                            } else if(handler.parameterCount == 4) {
                                handler.invoke(commandClass, sender, cmd, label, args)
                            }
                        }else {
                            sender.sendMessage(plugin.getMessage("no_permission")
                                .replace("\$permission", permission))
                        }
                    }else {
                        val permission = handler.getAnnotation(Subcommand::class.java).permission
                        if(sender.hasPermission(permission)) {
                            if (handler.parameterCount == 2) {
                                handler.invoke(commandClass, sender, args)
                            } else if(handler.parameterCount == 4) {
                                handler.invoke(commandClass, sender, cmd, label, args)
                            }
                        }else {
                            sender.sendMessage(plugin.getMessage("no_permission")
                                .replace("\$permission", permission))
                        }
                    }
                    false
                }
                if(plCommand?.tabCompleter == null) {
                    plCommand?.setTabCompleter { _, _, _, args ->
                        val completions = ArrayList<String>()
                        registeredCommands.filter {
                            it.key.startsWith("${command.command}/")
                                    && it.value.second.getAnnotation(Subcommand::class.java) != null
                                    && it.value.second.getAnnotation(Subcommand::class.java).position == args.size
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
            }else if(method.getAnnotation(Subcommand::class.java) != null) {
                val subcommand = method.getAnnotation(Subcommand::class.java)
                val completionFormat = if(method.getAnnotation(TabCompletion::class.java) != null) {
                    method.getAnnotation(TabCompletion::class.java).format
                }else {
                    ""
                }
                registeredCommands["${subcommand.rootCommand}/${subcommand.subCommand}"] = Pair(completionFormat, method)
            }else if(method.getAnnotation(de.neo.modernbukkit.command.annotation.TabCompleter::class.java) != null) {
                val tabCompleter = method.getAnnotation(de.neo.modernbukkit.command.annotation.TabCompleter::class.java)
                pluginCommands[tabCompleter.command]?.tabCompleter =
                    TabCompleter { sender, cmd, alias, args ->
                        if(method.returnType is List<*>) {
                            if(method.parameterCount == 2) {
                                return@TabCompleter method.invoke(commandClass, sender, args) as List<String>
                            }else if (method.parameterCount == 4) {
                                return@TabCompleter method.invoke(commandClass, sender, cmd, alias, args) as List<String>
                            }
                        }
                        return@TabCompleter listOf<String>()
                    }
            }
        }
    }
}