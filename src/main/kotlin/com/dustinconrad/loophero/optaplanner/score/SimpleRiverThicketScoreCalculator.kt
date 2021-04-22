package com.dustinconrad.loophero.optaplanner.score

import com.dustinconrad.loophero.optaplanner.domain.*
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
            val riverNeighborCount = Direction.values().fold(0) { acc, dir ->
                acc + if (lookupMap[p.y + dir.deltaY to p.x + dir.deltaX]?.card is River) {
                    1
                } else {
                    0
                }
            }

            softScore += if (riverNeighborCount == 0) {
                2
            } else {
                riverNeighborCount * 4
            }
        }

        fun scoreRiver(p: Position) {
            //should have exactly 1 river pointing at it
            val sources = Direction.values().fold(0) { acc, dir ->
                val neighborCard = lookupMap[p.y + dir.deltaY to p.x + dir.deltaX]?.card
                acc + if (neighborCard is River && neighborCard.direction.reverse() == dir) {
                    1
                } else {
                    0
                }
            }

            // unless it is an edge
            if (p.isEdge && sources == 0) {
                riverStartCount++
            } else {
                hardScore -= abs(1 - sources)
            }

            //should not be pointing at what is pointing at it
            val pointingAt = lookupMap[(p.y to p.x) + (p.card as River).direction]

            if (pointingAt?.card is River && (pointingAt.card as River).direction == (p.card as River).direction.reverse()) {
                hardScore--
            }
        }

        for (p in solution.shape) {
            when(p.card) {
                is Thicket -> scoreThicket(p)
                is River -> scoreRiver(p)
            }
        }

        // at most 1 start river
        hardScore -= if (riverStartCount > 1) {
            abs(1 - riverStartCount)
        } else {
            0
        }

        // should be 1 contiguous blob
        val rivers = solution.shape
            .filter { it.card is River }
            .map { it.y to it.x }
            .toMutableSet()

        var islands = 0
        while(rivers.isNotEmpty()) {
            islands++
            val initial = rivers.first()
            rivers.remove(initial)
            val q = ArrayDeque(listOf(initial))
            while(q.isNotEmpty()) {
                val river = q.removeFirst()

                Direction.values().forEach {
                    val neighbor = river + it
                    if(rivers.remove(neighbor)) {
                        q.add(neighbor)
                    }
                }
            }
        }
        hardScore -= abs(1 - islands)

        return HardSoftScore.of(hardScore, softScore)
    }

}