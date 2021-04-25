package community.flock.common

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam

@Path("/{uuid}")
data class UuidParam(@PathParam("Try with: f04e5e8a-7b8d-4519-9a8b-8d04101086f7") val uuid: String)
