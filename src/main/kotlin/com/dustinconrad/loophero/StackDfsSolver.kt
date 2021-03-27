package com.dustinconrad.loophero

import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

data class State(
    val board: Board,
    val nextPos: Pair<Int, Int>
)

private fun maximizeStackDfs(acc: AtomicReference<Board>, initialBoard: Board, initialY: Int, initialX: Int) {
    val stack = ArrayDeque<State>(initialBoard.width * initialBoard.height)

    stack.addLast(State(initialBoard.copy(), initialY to initialX))

    while(!stack.isEmpty()) {
        val (currBoard, nextPos) = stack.removeLast()
        val (nextY, nextX) = nextPos
        currBoard[nextY, nextX] = Card.RIVER

        if (currBoard > acc.get()) {
            acc.accumulateAndGet(currBoard.copy(), ::maxOf)
        }

        var addCount = 0;

        fun checkAndAdd(y: Int, x: Int) {
            if (currBoard[y, x] == Card.THICKET) {
                if (addCount++ == 0) {
                    stack.addLast(State(currBoard, y to x))
                } else {
                    stack.addLast(State(currBoard.copy(), y to x))
                }
            }
        }

        if (nextY > 0) {
            checkAndAdd(nextY - 1, nextX)
        }
        if (nextY < currBoard.height - 1) {
            checkAndAdd(nextY + 1, nextX)
        }
        if (nextX > 0) {
            checkAndAdd(nextY, nextX - 1)
        }
        if (nextX < currBoard.width - 1) {
            checkAndAdd(nextY, nextX + 1)
        }

    }
}

private fun maximizeStackAsyncEntry(acc: AtomicReference<Board>, board: Board, startY: Int, startX: Int): CompletableFuture<Void> {
    return CompletableFuture.runAsync {
        maximizeStackDfs(acc, board, startY, startX)
    }
}

@ExperimentalTime
fun main() {
    val height = 8
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
//        startPositions.forEach { maximizeStackDfs(max, board.copy(), it.first, it.second) }

        val results = startPositions.map { maximizeStackAsyncEntry(max, board.copy(), it.first, it.second) }
        results.forEach { it.join() }
    }

    println("Score: ${max.get().score}")
    println(max.get().toString())
    println("Took $time")
}