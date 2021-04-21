package com.dustinconrad.loophero.optaplanner

import com.dustinconrad.loophero.optaplanner.domain.*
import com.dustinconrad.loophero.optaplanner.score.SimpleRiverThicketScoreCalculator
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig
import org.optaplanner.core.config.solver.SolverConfig
import org.optaplanner.core.impl.exhaustivesearch.ExhaustiveSearchPhase
import java.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


@ExperimentalTime
fun main() {

    val solverFactory = SolverFactory.create<Grid>(
        SolverConfig()
            .withSolutionClass(Grid::class.java)
            .withEntityClasses(Position::class.java)
            .withEasyScoreCalculatorClass(SimpleRiverThicketScoreCalculator::class.java)
            .withTerminationSpentLimit(Duration.ofSeconds(2))
    )

    val solver = solverFactory.buildSolver()

    val positions = createPositions(5, 5)

    val availableCards = rivers + Thicket

    val grid = Grid(availableCards, positions)

    val result: Grid
    val time = measureTime {
        result = solver.solve(grid)
    }
    println("Took $time")
    println(result)
}