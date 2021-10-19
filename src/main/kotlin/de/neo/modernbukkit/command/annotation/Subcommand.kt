package de.neo.modernbukkit.command.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Subcommand(val rootCommand: String, val subCommand: String, val permission: String, val position: Int = 1)
