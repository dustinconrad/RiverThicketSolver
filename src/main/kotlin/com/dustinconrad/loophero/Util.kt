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

fun Byte.lowerNibble(): Int =
    (this.toInt() and 0xF) shl 28 shr 28

fun Byte.upperNibble(): Int {
    return (this.toInt() and -16) shr 4
}

fun Byte.setLowerNibble(nibble: Int): Int {
    return ((this.toInt() and -16) or (0xF and nibble))
}

fun Byte.setUpperNibble(nibble: Int): Int {
    return (this.toInt() and 0xF) or (nibble shl 4)
}