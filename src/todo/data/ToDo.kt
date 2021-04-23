package community.flock.todo.data

import community.flock.common.Externalizable
import java.time.LocalDateTime
import java.util.UUID

data class ToDo(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?
) : Externalizable<PersistedToDo> {

    constructor(toDo: PersistedToDo) : this(
        UUID.fromString(toDo.id),
        toDo.title,
        toDo.description,
        toDo.completed,
        LocalDateTime.parse(toDo.createdAt),
        LocalDateTime.parse(toDo.dueDate),
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

data class PersistedToDo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
)

fun PersistedToDo.internalize() = ToDo(this)
