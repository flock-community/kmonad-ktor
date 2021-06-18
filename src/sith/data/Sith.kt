package community.flock.sith.data

import community.flock.common.define.Data
import java.util.UUID

data class Sith(
    override val id: String = UUID.randomUUID().toString(),
    val name: String,
    val age: Int
) : Data
