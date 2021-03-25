package com.dustinconrad.loophero

import java.lang.IllegalArgumentException
import java.util.concurrent.CompletableFuture
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun maximizeRecursiveDfs(board: Board, startY: Int, startX: Int): Board {
    if (board[startY, startX] == Card.RIVER) {
        return board.copy()
    }
    board[startY, startX] = Card.RIVER

    val neighborCandidates = ArrayList<Board>(5)
    neighborCandidates.add(board)

    fun checkAndAdd(y: Int, x: Int) {
        if (board[y, x] == Card.THICKET) {
            neighborCandidates.add(maximizeRecursiveDfs(board, y, x))
        }
    }

    if (startY > 0) {
        checkAndAdd(startY - 1, startX)
    }
    if (startY < board.height - 1) {
        checkAndAdd(startY + 1, startX)
    }
    if (startX > 0) {
        checkAndAdd(startY, startX - 1)
    }
    if (startX < board.width - 1) {
        checkAndAdd(startY, startX + 1)
    }

    val toReturn = neighborCandidates.maxOrNull()?.copy() ?: throw IllegalArgumentException("Unexpected state")

    board[startY, startX] = Card.THICKET

    return toReturn
}

fun maximizeRecursiveDfsAsyncEntry(board: Board, startY: Int, startX: Int): CompletableFuture<Board> {
    return CompletableFuture.supplyAsync {
        maximizeRecursiveDfs(board, startY, startX)
    }
}

@ExperimentalTime
fun main() {
    val height = 9
    val width = 5

    val board = ArrayBoard(height, width)

    val startPositions = mutableSetOf<Pair<Int,Int>>()

    for (y in 0 until (height / 2.0 + 0.5).toInt()) {
        startPositions.add(y to 0)
    }

    for (x in 0 until (width / 2.0 + 0.5).toInt()) {
        startPositions.add(0 to x)
    }

    var max: Board?
    val time = measureTime {
//        max = startPositions.map { maximize(board.copy(), it.first, it.second) }
//            .maxOrNull()

        val results = startPositions.map { maximizeRecursiveDfsAsyncEntry(board.copy(), it.first, it.second) }

        max = results.map { it.join() }
            .maxOrNull()
    }

    println("Score: ${max?.score}")
    println(max.toString())
    println("Took $time")
}