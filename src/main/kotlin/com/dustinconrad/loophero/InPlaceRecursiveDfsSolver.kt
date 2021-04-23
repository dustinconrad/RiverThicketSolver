package com.dustinconrad.loophero

import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private fun maximizeInPlaceRecursiveDfs(acc: AtomicReference<Board>, board: Board, startY: Int, startX: Int) {
    board[startY, startX] = Card.RIVER
    if (board > acc.get()) {
        acc.accumulateAndGet(board.copy(), ::maxOf)
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

private fun maximizeInPlaceRecursiveDfsAsyncEntry(acc: AtomicReference<Board>, board: Board, startY: Int, startX: Int): CompletableFuture<Void> {
    return CompletableFuture.runAsync {
        maximizeInPlaceRecursiveDfs(acc, board, startY, startX)
    }
}

@ExperimentalTime
fun main() {
    val height = 8
    val width = 5

    val board = ByteArrayBoard(height, width)

    val startPositions = startPositions(board)

    val max = AtomicReference<Board>(board.copy())
    val time = measureTime {
//        startPositions.forEach { maximizeInPlaceRecursiveDfs(max, board.copy(), it.first, it.second) }

        val results = startPositions.map { maximizeInPlaceRecursiveDfsAsyncEntry(max, board.copy(), it.first, it.second) }
        results.forEach { it.join() }
    }

    println("Score: ${max.get().score}")
    println(max.get().toString())
    println("Took $time")
}