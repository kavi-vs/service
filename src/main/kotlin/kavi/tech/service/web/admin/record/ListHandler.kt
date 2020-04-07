package kavi.tech.service.web.admin.record

import io.vertx.core.http.HttpMethod
import io.vertx.core.json.DecodeException
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
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
import kavi.tech.service.mongo.model.RecordModel
import kavi.tech.service.mysql.dao.UserDao
import kavi.tech.service.mysql.entity.User

@HandlerRequest(path = "/list", method = HttpMethod.GET)
class ListHandler @Autowired constructor(
        private val recordModel: RecordModel
) : ControllerHandler() {

    /**
     * 数据处理
     * */
    override fun handle(routingContext: RoutingContext) {
        recordModel.list(JsonObject(), FindOptions())
            .subscribe({
                routingContext.response().success(it)
            }, routingContext::fail)
    }
}