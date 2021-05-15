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
) : Exposable<ExposedToDo>, Externalizable<PersistedToDo> {

    constructor(toDo: PotentialToDo) : this(
        id = UUID.fromString(toDo.id),
        title = toDo.title,
        description = toDo.description,
        completed = toDo.completed,
        createdAt = LocalDateTime.parse(toDo.createdAt),
        dueDate = LocalDateTime.parse(toDo.dueDate)
    )

    constructor(toDo: PersistedToDo) : this(
        UUID.fromString(toDo.id),
        toDo.title,
        toDo.description,
        toDo.completed,
        LocalDateTime.parse(toDo.createdAt),
        toDo.dueDate?.let { LocalDateTime.parse(it) }
    )

    override fun expose() = ExposedToDo(
        id.toString(),
        title,
        description,
        completed,
        createdAt.toString(),
        dueDate?.toString()
    )

    override fun externalize() = PersistedToDo(
        id.toString(),
        title,
        description,
        completed,
        createdAt.toString(),
        dueDate?.toString()
    )
}

data class PotentialToDo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

data class ExposedToDo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

data class PersistedToDo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

fun PotentialToDo.consume() = Todo(this)
fun PersistedToDo.internalize() = Todo(this)
