package community.flock.todo.data

import community.flock.common.define.Data
import community.flock.common.define.Exposable
import community.flock.common.define.Externalizable
import java.time.LocalDateTime
import java.util.UUID
import community.flock.todo.Todo as ExposedTodo
import community.flock.todo.Todo as PotentialTodo

data class Todo(
    override val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?
) : Data, Exposable<ExposedTodo>, Externalizable<PersistedTodo> {

    constructor(todo: PotentialTodo) : this(
        id = todo.id,
        title = todo.title,
        description = todo.description,
        completed = todo.completed,
        createdAt = LocalDateTime.parse(todo.createdAt),
        dueDate = LocalDateTime.parse(todo.dueDate)
    )

    constructor(todo: PersistedTodo) : this(
        todo.id,
        todo.title,
        todo.description,
        todo.completed,
        LocalDateTime.parse(todo.createdAt),
        todo.dueDate?.let { LocalDateTime.parse(it) }
    )

    override fun expose() = ExposedTodo(
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

fun PotentialTodo.consume() = Todo(this)
fun PersistedTodo.internalize() = Todo(this)
