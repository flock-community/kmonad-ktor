package community.flock.sith.data

import java.util.UUID

data class Sith(val name: String, val age: Int) {
    val id: String = UUID.randomUUID().toString()
}
