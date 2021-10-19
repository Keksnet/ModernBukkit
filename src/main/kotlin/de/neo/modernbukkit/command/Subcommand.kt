package de.neo.modernbukkit.command

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Subcommand(val rootCommand: String, val subCommand: String, val permission: String, val position: Int)
