package com.dustinconrad.loophero.optaplanner.domain

import com.dustinconrad.loophero.startPositions
import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.domain.variable.PlanningVariable
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore

import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import java.lang.IllegalStateException
import java.lang.StringBuilder

private const val availableCardsId = "availableCards"

@PlanningEntity
data class Position(
    val y: Int,
    val x: Int,
    val isEdge: Boolean
) {

    private constructor(): this(0, 0, false)

    @PlanningVariable(valueRangeProviderRefs = [availableCardsId])
    var card: Card? = null

}

@PlanningSolution
class Grid(
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = availableCardsId)
    val availableCards: List<Card> = emptyList(),

    @PlanningEntityCollectionProperty
    var shape: List<Position> = emptyList()
){

    @PlanningScore
    var score: HardSoftScore? = null

    override fun toString(): String {
        val lookupMap = shape.groupBy { it.y }
            .mapValues { (_, v) -> v.associateBy { it.x }.toSortedMap() }
            .toSortedMap()

        val rows = Array(lookupMap.lastKey() + 1) { "" }

        for (y in rows.indices) {
            rows[y] = if (lookupMap.containsKey(y)) {
                val row = lookupMap[y] ?: throw IllegalStateException("Unexpected row not present")
                val rowString = StringBuilder()
                for (x in 0..row.lastKey()) {
                    val t = row[x]?.card
                    rowString.append(t?.toChar() ?: '_')
                }
                rowString.toString()
            } else {
                ""
            }
        }

        return rows.joinToString("\n")
    }
}

fun createPositions(height: Int, width: Int): List<Position> {
    //val edgePositions = startPositions(height, width)

    val positions = mutableListOf<Position>()

    for (y in 0 until height) {
        for (x in 0 until width) {
            positions.add(
                Position(y, x, x == 0 || y == 0)
            )
        }
    }

    return positions
}

fun parseGrid(lines: List<String>): Grid {
    val positions = lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, c ->
            val p = Position(y, x, x == 0 || y == 0)
            val card = parseCard(c)
            p.card = card

            p
        }
    }

    val availableCards = positions.mapNotNull { it.card }
        .toSet()
        .toList()

    return Grid(availableCards, positions)
}

