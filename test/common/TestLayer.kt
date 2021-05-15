package common

import jedi.TestRepository as JediTestRepository
import sith.TestRepository as SithTestRepository
import todo.TestRepository as TodoTestRepository

object TestLayer {
    val jediRepository = JediTestRepository
    val sithRepository = SithTestRepository
    val todoRepository = TodoTestRepository
    val logger = TestLogger
}
