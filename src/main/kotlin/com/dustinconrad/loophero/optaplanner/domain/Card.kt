package com.dustinconrad.loophero.optaplanner.domain

private enum class Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST
}

sealed class Card private constructor() {
    abstract fun toChar(): Char
}

sealed class River private constructor(private val dir: Direction) : Card()

object NorthRiver : River(Direction.NORTH) {
    override fun toChar(): Char = '^'
}
object SouthRiver : River(Direction.SOUTH) {
    override fun toChar(): Char = 'v'
}
object EastRiver : River(Direction.EAST) {
    override fun toChar(): Char = '>'
}
object WestRiver : River(Direction.WEST) {
    override fun toChar(): Char = '<'
}

object Thicket : Card() {
    override fun toChar(): Char = 't'
}

val rivers = listOf(NorthRiver, SouthRiver, EastRiver, WestRiver)

private val allCards = (rivers + Thicket)
    .associateBy { it.toChar() }

fun parseCard(c: Char): Card {
    return allCards[c] ?: throw IllegalArgumentException("Unrecognized character for card: $c")
}