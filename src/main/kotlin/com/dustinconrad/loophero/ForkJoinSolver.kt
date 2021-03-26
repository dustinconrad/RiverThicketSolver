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
            acc.accumulateAndGet(board) { prev, x ->
                maxOf(prev, x)
            }
        }

        val subtasks = ArrayList<DfsAction>(4)

        fun checkAndAdd(y: Int, x: Int) {
            if (board[y, x] == Card.THICKET) {
                subtasks.add(DfsAction(acc, board.copy(), y, x))
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

    val board = ArrayBoard(height, width)

    val startPositions = mutableSetOf<Pair<Int,Int>>()

    for (y in 0 until (height / 2.0 + 0.5).toInt()) {
        startPositions.add(y to 0)
    }

    for (x in 0 until (width / 2.0 + 0.5).toInt()) {
        startPositions.add(0 to x)
    }

    val max = AtomicReference<Board>(board.copy())

    val initialTask = DfsEntry(max, board, startPositions)
    val pool = ForkJoinPool.commonPool()
    println("Parallellism: ${pool.parallelism}")

    val time = measureTime {
        pool.invoke(initialTask)
        pool.shutdown()

//        val results = startPositions.map { maximizeStackAsyncEntry(max, board.copy(), it.first, it.second) }
//        results.forEach { it.join() }
    }

    println("Score: ${max.get().score}")
    println(max.get().toString())
    println("Took $time")
}