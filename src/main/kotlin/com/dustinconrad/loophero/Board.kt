package com.dustinconrad.loophero

enum class Card(private val ts: String) {
    RIVER("r"),
    THICKET("#");

    override fun toString(): String {
        return ts
    }
}

interface Board : Comparable<Board> {

    val height: Int

    val width: Int

    operator fun get(y: Int, x: Int): Card

    operator fun set(y: Int, x: Int, card: Card)

    val score: Int

    fun copy(): Board

    override fun compareTo(other: Board): Int {
        return this.score.compareTo(other.score)
    }
}

abstract class NeighborCountingBoard(
    override val height: Int,
    override val width: Int,
    private var _score: Int,
) : Board {

    abstract protected fun rawGet(y: Int, x: Int): Byte

    abstract protected fun rawSet(y: Int, x: Int, value: Byte)

    override fun get(y: Int, x: Int): Card =
        if (rawGet(y, x) < 0) {
            Card.RIVER
        } else  {
            Card.THICKET
        }

    override fun set(y: Int, x: Int, card: Card) {
        val toReplace = this[y, x ]
        if (card != toReplace) {
            val countMod = if (card == Card.RIVER) 1 else -1
            var prevSubScore = scoreTile(rawGet(y, x).toInt())
            var newSubScore = 0

            var neighborRivers = 0

            fun neighborCheck(nY: Int, nX: Int) {
                if (this[nY, nX] == Card.RIVER) {
                    neighborRivers++
                } else {

                    val prevRiverCount = rawGet(nY, nX)
                    prevSubScore += scoreTile(prevRiverCount.toInt())
                    newSubScore += scoreTile(prevRiverCount + countMod)

                    rawSet(nY, nX, (prevRiverCount + countMod).toByte())
                }
            }

            if (y > 0) {
                neighborCheck(y - 1, x)
            }
            if (y < height - 1) {
                neighborCheck(y + 1, x)
            }
            if (x > 0) {
                neighborCheck(y, x - 1)
            }
            if (x < width - 1) {
                neighborCheck(y, x + 1)
            }

            newSubScore += if (card == Card.THICKET) {
                scoreTile(neighborRivers)
            } else {
                0
            }
            _score += newSubScore - prevSubScore

            rawSet(y, x,
                if (card == Card.RIVER) {
                    (-1).toByte()
                } else {
                    neighborRivers.toByte()
                }
            )
        }
    }

    override val score: Int
        get() = _score

    private fun scoreTile(riverCount: Int) =
        when {
            riverCount < 0 -> 0
            riverCount == 0 -> 2
            else -> riverCount * 4
        }

    override fun toString(): String {
        return (0 until height)
            .map { y -> (0 until width).map { x -> this[y, x].toString() } }
            .joinToString("\n") { it.joinToString("") }
    }
}

class ArrayBoard(
    height: Int,
    width: Int,
    score: Int,
    private val board: ByteArray,
): NeighborCountingBoard(height, width, score) {

    constructor(height: Int, width: Int): this(height, width, height * width * 2, ByteArray(height * width))

    override fun rawGet(y: Int, x: Int): Byte = board[y * width + x]

    override fun rawSet(y: Int, x: Int, value: Byte) {
        board[y * width + x] = value
    }

    override fun copy(): Board {
        return ArrayBoard(height, width, score, board.copyOf())
    }

}