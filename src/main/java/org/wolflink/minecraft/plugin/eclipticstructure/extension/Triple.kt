package org.wolflink.minecraft.plugin.eclipticstructure.extension

fun<A,B,C> Triple<A,B,C>.deepEquals(another: Triple<A,B,C>) =
    first?.equals(another.first) == true
            && second?.equals(another.second) == true
            && third?.equals(another.third) == true