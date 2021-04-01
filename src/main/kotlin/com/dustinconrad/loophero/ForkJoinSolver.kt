package com.dustinconrad.loophero

import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveAction
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import java.util.concurrent.ForkJoinPool

class DfsAction(
    private val acc: AtomicReference<Board>,
    private val board: Board,
    private val nextY: Int,
    private val nextX: Int
) : RecursiveAction() {

    override fun compute() {
        board[nextY, nextX] = Card.RIVER

        if (board > acc.get()) {
            acc.accumulateAndGet(board.copy(), ::maxOf)
        }

        val subtasks = ArrayList<DfsAction>(4)

        fun checkAndAdd(y: Int, x: Int) {
            if (board[y, x] == Card.THICKET) {
                if (subtasks.isEmpty()) {
                    subtasks.add(DfsAction(acc, board, y, x))
                } else {
                    subtasks.add(DfsAction(acc, board.copy(), y, x))
                }
            }
        }

        if (nextY > 0) {
            checkAndAdd(nextY - 1, nextX)
        }
        if (nextY < board.height - 1) {
            checkAndAdd(nextY + 1, nextX)
        }
        if (nextX > 0) {
            checkAndAdd(nextY, nextX - 1)
        }
        if (nextX < board.width - 1) {
            checkAndAdd(nextY, nextX + 1)
        }

        ForkJoinTask.invokeAll(subtasks)
    }

}

class DfsEntry(
    private val acc: AtomicReference<Board>,
    private val board: Board,
    private val startTile: Collection<Pair<Int, Int>>
) : RecursiveAction() {

    override fun compute() {
        val subTasks = startTile.map { DfsAction(acc, board.copy(), it.first, it.second) }

        ForkJoinTask.invokeAll(subTasks)
    }

    fun result(): Board {
        return acc.get()
    }

}

@ExperimentalTime
fun main() {
    val height = 8
    val width = 5
    val parallellism = ForkJoinPool.getCommonPoolParallelism()

    val board = ByteArrayBoard(height, width)

    val startPositions = startPositions(board)

    val max = AtomicReference<Board>(board.copy())

    val initialTask = DfsEntry(max, board, startPositions)
    val pool = ForkJoinPool(parallellism)

    val time = measureTime {
        pool.invoke(initialTask)
    }
    pool.shutdown()

    println("Score: ${max.get().score}")
    println(max.get().toString())
    println("Took $time")

    println()
    println("Pool")
    println("Parallellism: ${pool.parallelism}")
    println("Steal count: ${pool.stealCount}")
}