package de.neo.modernbukkit.command.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TabCompleter(val command: String)
