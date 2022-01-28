package de.neo.modernbukkit.util

class Universal(private val objectVal: Any) {

    fun asRaw() = objectVal
    fun asString() = objectVal as String
    fun asInt() = objectVal as Int
    fun asLong() = objectVal as Long
    fun asFloat() = objectVal as Float
    fun asDouble() = objectVal as Double
    fun asBool() = objectVal as Boolean

}