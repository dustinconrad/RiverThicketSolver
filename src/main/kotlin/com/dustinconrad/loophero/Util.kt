package com.dustinconrad.loophero

fun startPositions(board: Board): Set<Pair<Int, Int>> {
    val startPositions = mutableSetOf<Pair<Int,Int>>()

    for (y in 0 until (board.height / 2.0 + 0.5).toInt()) {
        startPositions.add(y to 0)
    }

    for (x in 0 until (board.width / 2.0 + 0.5).toInt()) {
        startPositions.add(0 to x)
    }

    return startPositions
}