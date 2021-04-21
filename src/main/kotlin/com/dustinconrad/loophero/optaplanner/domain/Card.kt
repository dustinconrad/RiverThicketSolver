package com.dustinconrad.loophero.optaplanner.domain

enum class Direction(val deltaY: Int, val deltaX: Int) {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1),
}

fun Direction.reverse() = when(this) {
    Direction.NORTH -> Direction.SOUTH
    Direction.SOUTH -> Direction.NORTH
    Direction.EAST -> Direction.WEST
    Direction.WEST -> Direction.EAST
}

operator fun Pair<Int, Int>.plus(dir: Direction): Pair<Int, Int> {
    return this.first + dir.deltaY to this.second + dir.deltaX
}

sealed class Card private constructor() {
    abstract fun toChar(): Char
}

sealed class River private constructor(val direction: Direction) : Card()

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