package com.dustinconrad.loophero

import java.util.concurrent.atomic.AtomicReference
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private fun maximizeInPlaceRecursiveDfs(acc: AtomicReference<Board>, board: Board, startY: Int, startX: Int) {
    board[startY, startX] = Card.RIVER
    if (board > acc.get()) {
        acc.accumulateAndGet(board.copy()) { prev, x ->
            maxOf(prev, x)
        }
    }

    fun checkAndAdd(y: Int, x: Int) {
        if (board[y, x] == Card.THICKET) {
            maximizeInPlaceRecursiveDfs(acc, board, y, x)
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

    board[startY, startX] = Card.THICKET
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

    val max = AtomicReference<Board>(board.copy())
    val time = measureTime {
        startPositions.forEach { maximizeInPlaceRecursiveDfs(max, board.copy(), it.first, it.second) }

//        val results = startPositions.map { maximizeRecursiveDfsAsyncEntry(board.copy(), it.first, it.second) }
//
//        max = results.map { it.join() }
//            .maxOrNull()
    }

    println("Score: ${max.get().score}")
    println(max.get().toString())
    println("Took $time")
}