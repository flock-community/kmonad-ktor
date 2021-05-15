package community.flock.common

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam

@Path("/{uuid}")
data class UuidParam(@PathParam("UUID") val uuid: String)
