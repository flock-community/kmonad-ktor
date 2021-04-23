package community.flock.todo.data

import java.util.UUID

data class Todo(
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: String,
    val dueDate: String?
) {
    val id: String = UUID.randomUUID().toString()
}
