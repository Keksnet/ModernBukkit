package de.neo.modernbukkit.command

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TabCompleter(val command: String)
