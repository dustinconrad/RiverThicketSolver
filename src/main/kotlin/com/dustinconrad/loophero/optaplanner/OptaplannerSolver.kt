package com.dustinconrad.loophero.optaplanner

import com.dustinconrad.loophero.optaplanner.domain.*
import com.dustinconrad.loophero.optaplanner.score.SimpleRiverThicketScoreCalculator
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.config.solver.SolverConfig
import java.time.Duration


fun main() {

    val solverFactory = SolverFactory.create<Grid>(
        SolverConfig()
            .withSolutionClass(Grid::class.java)
            .withEntityClasses(Position::class.java)
            .withEasyScoreCalculatorClass(SimpleRiverThicketScoreCalculator::class.java)
            .withTerminationSpentLimit(Duration.ofMinutes(1))
    )

    val solver = solverFactory.buildSolver()

    val positions = createPositions(3, 3)

    val availableCards = listOf(*rivers.toTypedArray(), Thicket)

    val grid = Grid(availableCards, positions)

    val result = solver.solve(grid)
}