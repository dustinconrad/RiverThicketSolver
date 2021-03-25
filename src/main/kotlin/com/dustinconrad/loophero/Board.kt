package com.dustinconrad.loophero

import java.lang.IllegalArgumentException

typealias Coord = Pair<Int, Int>

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

class ArrayBoard(
    override val height: Int,
    override val width: Int) : Board {

    private var board = ByteArray(height * width) { 0 }

    private var _score = height * width * 2

    private constructor(board: ArrayBoard): this(board.height, board.width) {
        this.board = board.board.copyOf()
        this._score = board._score
    }

    override fun get(y: Int, x: Int): Card =
        if (board[y * width + x] < 0) {
            Card.RIVER
        } else  {
            Card.THICKET
        }

    override fun set(y: Int, x: Int, card: Card) {
        val toReplace = this[y, x ]
        if (card != toReplace) {
            val countMod = if (card == Card.RIVER) 1 else -1
            var prevSubScore = scoreTile(board[y * width + x].toInt())
            var newSubScore = 0

            var neighborRivers = 0

            fun neighborCheck(nY: Int, nX: Int) {
                if (this[nY, nX] == Card.RIVER) {
                    neighborRivers++
                } else {

                    val prevRiverCount = board[nY * width + nX]
                    prevSubScore += scoreTile(prevRiverCount.toInt())
                    newSubScore += scoreTile(prevRiverCount + countMod)

                    board[nY * width + nX] = (prevRiverCount + countMod).toByte()
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

            board[y * width + x] = if (card == Card.RIVER) {
                (-1).toByte()
            } else {
                neighborRivers.toByte()
            }
        }
    }

    override val score: Int
        get() = _score

    override fun copy(): Board {
        return ArrayBoard(this)
    }

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