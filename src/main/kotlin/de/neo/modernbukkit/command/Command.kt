package de.neo.modernbukkit.command

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Command(val command: String, val permission: String, vararg val alias: String = [])
