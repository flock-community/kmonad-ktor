package community.flock.common.define

import community.flock.todo.pipe.Repository

sealed interface Has {

    interface TodoRepository {
        val toDoRepository: Repository
    }

}
