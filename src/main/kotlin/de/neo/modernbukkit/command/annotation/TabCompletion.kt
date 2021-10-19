package de.neo.modernbukkit.command.annotation

import java.util.function.Function
import java.util.function.Supplier

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TabCompletion(val format: String)
