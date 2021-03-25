package com.dustinconrad.loophero

import java.lang.IllegalArgumentException
import java.util.concurrent.CompletableFuture
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun maximize(board: Board, startY: Int, startX: Int): Board {
    if (board[startY, startX] == Card.RIVER) {
        return board.copy()
    }
    board[startY, startX] = Card.RIVER

    val neighborCandidates = ArrayList<Board>(5)
    neighborCandidates.add(board)

    fun checkAndAdd(y: Int, x: Int) {
        if (board[y, x] == Card.THICKET) {
            neighborCandidates.add(maximize(board, y, x))
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

    val toReturn = neighborCandidates.maxByOrNull { it.score }?.copy() ?: throw IllegalArgumentException("Unexpected state")

    board[startY, startX] = Card.THICKET

    return toReturn
}

fun maximizeAsyncEntry(board: Board, startY: Int, startX: Int): CompletableFuture<Board> {
    return CompletableFuture.supplyAsync {
        maximize(board, startY, startX)
    }
}

@ExperimentalTime
fun main() {
    val height = 7
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
        max = startPositions.map { maximize(board.copy(), it.first, it.second) }
            .maxByOrNull { it.score }

//        val results = startPositions.map { maximizeAsyncEntry(board.copy(), it.first, it.second) }
//
//        val max = results.map { it.join() }
//            .maxByOrNull { it.score }
//        println("Score: ${max?.score}")
//        println(max.toString())
    }

    println("Score: ${max?.score}")
    println(max.toString())
    println("Took $time")
}