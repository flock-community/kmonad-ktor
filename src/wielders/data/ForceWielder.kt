package community.flock.wielders.data

import community.flock.jedi.data.Jedi
import community.flock.sith.data.Sith

data class ForceWielder(
    val id: String,
    val name: String,
    val age: Int,
    val forceType: Type,
) {

    constructor(jedi: Jedi) : this(jedi.id, jedi.name, jedi.age, Type.LIGHT)

    constructor(sith: Sith) : this(sith.id, sith.name, sith.age, Type.DARK)

    enum class Type {
        DARK, LIGHT
    }

}

fun Jedi.toForceWielder() = ForceWielder(this)
fun Sith.toForceWielder() = ForceWielder(this)
