package com.dustinconrad.loophero.optaplanner.domain

private enum class Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST
}

sealed class Card private constructor()

sealed class River private constructor(private val dir: Direction) : Card()

object NorthRiver : River(Direction.NORTH) {
    override fun toString(): String = "^"
}
object SouthRiver : River(Direction.SOUTH) {
    override fun toString(): String = "v"
}
object EastRiver : River(Direction.EAST) {
    override fun toString(): String = ">"
}
object WestRiver : River(Direction.WEST) {
    override fun toString(): String = "<"
}

object Thicket : Card() {
    override fun toString(): String = "t"
}

val rivers = listOf(NorthRiver, SouthRiver, EastRiver, WestRiver)