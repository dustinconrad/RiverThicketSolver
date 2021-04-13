package com.dustinconrad.loophero.optaplanner.domain

private enum class Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST
}

sealed class Card private constructor()

sealed class River private constructor(private val dir: Direction) : Card()

object NorthRiver : River(Direction.NORTH)
object SouthRiver : River(Direction.SOUTH)
object EastRiver : River(Direction.EAST)
object WestRiver : River(Direction.WEST)

object Thicket : Card()