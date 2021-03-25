package com.dustinconrad.loophero

import java.util.concurrent.CompletableFuture
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun maximize(board: Board, startY: Int, startX: Int): Board {
    if (board[startY, startX] == Card.RIVER) {
        return board.copy()
    }
    var max = board
    board[startY, startX] = Card.RIVER

    val maxNeighbor = sequenceOf(
        startY - 1 to startX,
        startY + 1 to startX,
        startY to startX + 1,
        startY to startX - 1)
        .filter { it.first >= 0 && it.first < board.height }
        .filter { it.second >= 0 && it.second < board.width }
        .filter{ board[it.first, it.second] == Card.THICKET }
        .map { maximize(board, it.first, it.second) }
        .maxByOrNull { it.score }

    if (maxNeighbor != null && maxNeighbor.score > max.score) {
        max = maxNeighbor
    }

    val toReturn = max.copy()

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

    val time = measureTime {
        val max = startPositions.map { maximize(board.copy(), it.first, it.second) }
            .maxByOrNull { it.score }
        println("Score: ${max?.score}")
        println(max.toString())

//        val results = startPositions.map { maximizeAsyncEntry(board.copy(), it.first, it.second) }
//
//        val max = results.map { it.join() }
//            .maxByOrNull { it.score }
//        println("Score: ${max?.score}")
//        println(max.toString())
    }

    println("Took $time")
}