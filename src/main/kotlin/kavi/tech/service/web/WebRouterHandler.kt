package kavi.tech.service.web

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.impl.HttpStatusException
import kavi.tech.service.common.extension.error
import kavi.tech.service.common.extension.logger
import org.springframework.stereotype.Component
import java.io.FileNotFoundException
import javax.xml.bind.ValidationException

@Component
class WebRouterHandler{

    private val log = logger(this::class)

    /**
     * 全局路由处理
     * */
    fun routerHandler(routingContext: RoutingContext)  {
        // todo 访问日志 / 输出日志
        routingContext.next()
    }

    /**
     * 全局错误处理
     * */
    fun failureHandler(routingContext: RoutingContext)  {
        val e: Throwable? = routingContext.failure()
        val code = when (e) {
            is FileNotFoundException -> HttpResponseStatus.NOT_FOUND.code()
            is SecurityException -> HttpResponseStatus.UNAUTHORIZED.code()
            is ValidationException -> HttpResponseStatus.BAD_REQUEST.code()
            is HttpStatusException -> e.statusCode
            else ->
                if (routingContext.statusCode() > 0) {
                    routingContext.statusCode()
                } else {
                    500
                }
        }
        log.error("WebFailureHandler",e)
        routingContext.response().setStatusCode(code).error(e ?: UnknownError("Server Error"))
    }
}