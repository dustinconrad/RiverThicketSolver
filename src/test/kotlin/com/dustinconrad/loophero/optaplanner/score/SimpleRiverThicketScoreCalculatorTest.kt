package com.dustinconrad.loophero.optaplanner.score

import com.dustinconrad.loophero.optaplanner.domain.parseGrid
import kotlin.test.Test
import kotlin.test.assertEquals

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

}