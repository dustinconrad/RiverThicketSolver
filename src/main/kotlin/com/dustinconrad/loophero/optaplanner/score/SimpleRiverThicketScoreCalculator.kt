package com.dustinconrad.loophero.optaplanner.score

import com.dustinconrad.loophero.optaplanner.*
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator
import kotlin.math.abs

class SimpleRiverThicketScoreCalculator : EasyScoreCalculator<Grid, HardSoftScore> {

    override fun calculateScore(solution: Grid): HardSoftScore {
        val lookupMap = solution.shape.associateBy { (it.y to it.x) }

        var softScore = 0
        var hardScore = 0
        var riverStartCount = 0

        fun scoreThicket(p: Position) {
            var riverNeighborCount = 0
            if (lookupMap[p.y - 1 to p.x]?.card is River) {
                riverNeighborCount++
            }
            if (lookupMap[p.y + 1 to p.x]?.card is River) {
                riverNeighborCount++
            }
            if (lookupMap[p.y to p.x - 1]?.card is River) {
                riverNeighborCount++
            }
            if (lookupMap[p.y to p.x + 1]?.card is River) {
                riverNeighborCount++
            }
            softScore += if (riverNeighborCount == 0) {
                2
            } else {
                riverNeighborCount * 4
            }
        }

        fun scoreRiver(p: Position) {
            var sources = 0
            //should have exactly 1 river pointing at it
            if (lookupMap[p.y - 1 to p.x]?.card is SouthRiver) {
                sources++
            }
            if (lookupMap[p.y + 1 to p.x]?.card is NorthRiver) {
                sources++
            }
            if (lookupMap[p.y to p.x - 1]?.card is EastRiver) {
                sources++
            }
            if (lookupMap[p.y to p.x + 1]?.card is WestRiver) {
                sources++
            }
            // unless it is an edge
            if (p.isEdge && sources == 0) {
                riverStartCount++
            } else {
                hardScore -= abs(1 - sources)
            }

            //should not be pointing at what is pointing at it
            when(p.card) {
                is NorthRiver -> if (lookupMap[p.y - 1 to p.x]?.card is SouthRiver) {
                    hardScore--
                }
                is SouthRiver -> if (lookupMap[p.y + 1 to p.x]?.card is NorthRiver) {
                    hardScore--
                }
                is WestRiver -> if (lookupMap[p.y to p.x - 1]?.card is EastRiver) {
                    hardScore--
                }
                is EastRiver -> if (lookupMap[p.y to p.x + 1]?.card is WestRiver) {
                    hardScore--
                }
            }

        }

        // exactly 1 start river
        hardScore -= abs(1 - riverStartCount)

        for (p in solution.shape) {
            when(p.card) {
                is Thicket -> scoreThicket(p)
                is River -> scoreRiver(p)
            }
        }

        return HardSoftScore.of(hardScore, softScore)
    }

}