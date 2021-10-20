package community.flock.todo.data

import community.flock.common.ProducedAs
import community.flock.common.ExternalizedAs
import community.flock.kmonad.core.common.define.Data
import java.time.LocalDateTime
import java.util.UUID
import community.flock.todo.Todo as ProducedTodo
import community.flock.todo.Todo as PotentialTodo

data class Todo(
    override val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?
) : Data, ProducedAs<ProducedTodo>, ExternalizedAs<PersistedTodo> {

    override fun produce() = ProducedTodo(
        id,
        title,
        description,
        completed,
        createdAt.toString(),
        dueDate?.toString()
    )

    override fun externalize() = PersistedTodo(
        id,
        title,
        description,
        completed,
        createdAt.toString(),
        dueDate?.toString()
    )
}

data class PersistedTodo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

fun PotentialTodo.consume() = Todo(
    id,
    title,
    description,
    completed,
    LocalDateTime.parse(createdAt),
    LocalDateTime.parse(dueDate)
)

fun PersistedTodo.internalize() = Todo(
    id,
    title,
    description,
    completed,
    LocalDateTime.parse(createdAt),
    dueDate?.let { LocalDateTime.parse(it) }
)
