package kavi.tech.service.web.admin.user

import io.vertx.core.http.HttpMethod
import io.vertx.core.json.DecodeException
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import org.springframework.beans.factory.annotation.Autowired
import tech.kavi.vs.web.ControllerHandler
import tech.kavi.vs.web.HandlerRequest
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler
import io.vertx.ext.web.api.validation.ParameterType
import io.vertx.ext.web.handler.impl.HttpStatusException
import kavi.tech.service.common.extension.success
import kavi.tech.service.common.extension.toEntity
import kavi.tech.service.mysql.dao.UserDao
import kavi.tech.service.mysql.entity.User

@HandlerRequest(path = "/save", method = HttpMethod.POST)
class SaveHandler @Autowired constructor(
        private val userDao: UserDao
) : ControllerHandler() {

    /**
     * 参数校验过滤
     * */
    override fun route(route: Route) {
        route.handler(HTTPRequestValidationHandler.create()
            .addFormParam("username", ParameterType.GENERIC_STRING, true)
            .addFormParam("realname", ParameterType.GENERIC_STRING, true)
        )
    }

    /**
     * 数据处理
     * */
    override fun handle(routingContext: RoutingContext) {
        val user = try {
            routingContext.bodyAsJson.toEntity<User>()
        } catch (e: DecodeException) {
            routingContext.fail(HttpStatusException(400, e))
            return
        }
        userDao.save(user)
            .map { it.toJson() } // bean转JSON会自动忽略密码等信息
            .subscribe({
                routingContext.response().success(it)
            }, routingContext::fail)
    }
}