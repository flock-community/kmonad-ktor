package community.flock.wielders

import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.AppException
import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.LiveLogger
import community.flock.common.UuidParam
import community.flock.common.define.Logger
import community.flock.wielders.data.ForceWielder
import community.flock.wielders.define.Context
import community.flock.wielders.pipe.bindGet
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import community.flock.jedi.pipe.LiveRepository.Companion.liveRepository as liveJediRepository
import community.flock.sith.pipe.LiveRepository.Companion.liveRepository as liveSithRepository

@ExperimentalCoroutinesApi
@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val host = getProp("ktor.db.host", "localhost")
    val db = DataBase.instance(host)

    moduleWith(object : Context {
        override val jediRepository = db.liveJediRepository()
        override val sithRepository = db.liveSithRepository()
        override val logger: Logger = LiveLogger
    })

}

@ExperimentalCoroutinesApi
fun Application.moduleWith(context: Context) {
    apiRouting {
        route("/force-wielders")
            .throws(HttpStatusCode.InternalServerError, AppException.InternalServerError::class)
            .throws(HttpStatusCode.BadRequest, AppException.BadRequest::class)
            .throws(HttpStatusCode.NotFound, AppException.NotFound::class) {
                get<Unit, List<ForceWielder>> {
                    respond(context.bindGet().toList())
                }

                get<UuidParam, ForceWielder> { params ->
                    respond(context.bindGet(params.uuid))
                }
            }
    }
}
