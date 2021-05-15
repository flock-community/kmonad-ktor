package community.flock.todo.data

import community.flock.common.define.Exposable
import community.flock.common.define.Externalizable
import java.time.LocalDateTime
import java.util.UUID

data class Todo(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?
) : Exposable<ExposedTodo>, Externalizable<PersistedTodo> {

    constructor(todo: PotentialTodo) : this(
        id = UUID.fromString(todo.id),
        title = todo.title,
        description = todo.description,
        completed = todo.completed,
        createdAt = LocalDateTime.parse(todo.createdAt),
        dueDate = LocalDateTime.parse(todo.dueDate)
    )

    constructor(todo: PersistedTodo) : this(
        UUID.fromString(todo.id),
        todo.title,
        todo.description,
        todo.completed,
        LocalDateTime.parse(todo.createdAt),
        todo.dueDate?.let { LocalDateTime.parse(it) }
    )

    override fun expose() = ExposedTodo(
        id.toString(),
        title,
        description,
        completed,
        createdAt.toString(),
        dueDate?.toString()
    )

    override fun externalize() = PersistedTodo(
        id.toString(),
        title,
        description,
        completed,
        createdAt.toString(),
        dueDate?.toString()
    )
}

data class PotentialTodo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

data class ExposedTodo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

data class PersistedTodo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

fun PotentialTodo.consume() = Todo(this)
fun PersistedTodo.internalize() = Todo(this)
