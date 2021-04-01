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

    protected abstract fun rawGet(y: Int, x: Int): Int

    protected abstract fun rawSet(y: Int, x: Int, value: Int)

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
            var prevSubScore = scoreTile(rawGet(y, x))
            var newSubScore = 0

            var neighborRivers = 0

            fun neighborCheck(nY: Int, nX: Int) {
                val prevRiverCount = rawGet(nY, nX)
                if (prevRiverCount < 0) {
                    neighborRivers++
                } else {
                    prevSubScore += scoreTile(prevRiverCount)
                    newSubScore += scoreTile(prevRiverCount + countMod)

                    rawSet(nY, nX, prevRiverCount + countMod)
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

            newSubScore += if (card == Card.RIVER) {
                0
            } else {
                scoreTile(neighborRivers)
            }
            _score += newSubScore - prevSubScore

            rawSet(y, x,
                if (card == Card.RIVER) {
                    -1
                } else {
                    neighborRivers
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

    open fun debug(): String {
        return (0 until height)
            .map { y -> (0 until width).map { x -> rawGet(y, x).toString().padStart(2, ' ') } }
            .joinToString("\n") { it.joinToString(" ") }
    }
}

class ByteArrayBoard(
    height: Int,
    width: Int,
    score: Int,
    private val board: ByteArray,
): NeighborCountingBoard(height, width, score) {

    constructor(height: Int, width: Int): this(height, width, height * width * 2, ByteArray(height * width))

    override fun rawGet(y: Int, x: Int): Int = board[y * width + x].toInt()

    override fun rawSet(y: Int, x: Int, value: Int) {
        board[y * width + x] = value.toByte()
    }

    override fun copy(): Board {
        return ByteArrayBoard(height, width, score, board.copyOf())
    }

}

class IntArrayBoard(
    height: Int,
    width: Int,
    score: Int,
    private val board: IntArray,
): NeighborCountingBoard(height, width, score) {

    constructor(height: Int, width: Int): this(height, width, height * width * 2, IntArray(height * width))

    override fun rawGet(y: Int, x: Int): Int = board[y * width + x]

    override fun rawSet(y: Int, x: Int, value: Int) {
        board[y * width + x] = value
    }

    override fun copy(): Board {
        return IntArrayBoard(height, width, score, board.copyOf())
    }

}

class NibbleBoard(
    height: Int,
    width: Int,
    score: Int,
    val board: ByteArray,
): NeighborCountingBoard(height, width, score) {

    constructor(height: Int, width: Int): this(height, width, height * width * 2, ByteArray(((height * width)/2.0 + 0.5).toInt()))

    override fun rawGet(y: Int, x: Int): Int {
        val bucketIdx = (y * width + x) / 2
        val offset = (y * width + x) % 2
        val bucketVal = board[bucketIdx]
        return if (offset == 0) {
            bucketVal.upperNibble()
        } else {
            bucketVal.lowerNibble()
        }
    }

    override fun rawSet(y: Int, x: Int, value: Int) {
        val bucketIdx = (y * width + x) / 2
        val offset = (y * width + x) % 2
        val bucketVal = board[bucketIdx]
        if (offset == 0) {
            board[bucketIdx] = bucketVal.setUpperNibble(value).toByte()
        } else {
            board[bucketIdx] = bucketVal.setLowerNibble(value).toByte()
        }
    }

    override fun copy(): Board {
        return NibbleBoard(height, width, score, board.copyOf())
    }

}