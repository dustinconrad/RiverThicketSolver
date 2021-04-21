package com.dustinconrad.loophero.optaplanner.score

import com.dustinconrad.loophero.optaplanner.domain.parseGrid
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SimpleRiverThicketScoreCalculatorTest {

    private val sut = SimpleRiverThicketScoreCalculator()

    @Test
    fun test4x4_1() {
        val input = """
            t^<t
            vt^<
            >vt^
            t>>^
        """.trimIndent()

        val grid = parseGrid(input.lines())

        val score = sut.calculateScore(grid)

        assertEquals(0, score.hardScore, "Verify hard score")
        assertEquals(56, score.softScore, "Verify soft score")
    }

    @Test
    fun test5x5_invalid() {
        val input = """
            t>>vt
            >^t>v
            ^<tv<
            t^<<t
            vtttt
        """.trimIndent()

        val grid = parseGrid(input.lines())

        val score = sut.calculateScore(grid)

        assertNotEquals(0, score.hardScore, "Verify hard score")
        assertEquals(78, score.softScore, "Verify soft score")
    }

    @Test
    fun test5x5_valid() {
        val input = """
            t^<tt
            vt^<t
            >vt^<
            t>vt^
            tt>>^
        """.trimIndent()

        val grid = parseGrid(input.lines())

        val score = sut.calculateScore(grid)

        assertEquals(0, score.hardScore, "Verify hard score")
        assertEquals(92, score.softScore, "Verify soft score")
    }

}