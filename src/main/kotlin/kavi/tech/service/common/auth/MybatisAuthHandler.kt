package kavi.tech.service.common.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.web.handler.impl.AuthHandlerImpl
import io.vertx.ext.web.handler.impl.HttpStatusException
import kavi.tech.service.common.extension.success
import org.springframework.stereotype.Service

@Service
class MybatisAuthHandler(authProvider: AuthProvider): AuthHandlerImpl(authProvider) {
    /**
     * 数据访问验证
     * */
    override fun parseCredentials(context: RoutingContext, handler: Handler<AsyncResult<JsonObject>>) {
        val session = context.session()
        if (session != null) {
            handler.handle(Future.failedFuture<JsonObject>(HttpStatusException(401)))
        } else {
            handler.handle(Future.failedFuture("No session - did you forget to include a SessionHandler?"))
        }
    }

    /**
     * 表单登录验证
     * */
    fun login(context: RoutingContext)  {
        val req = context.request()
        if (req.method() != HttpMethod.POST) {
            context.fail(405)
            return
        }
        /**
         * 限制必须以表单形式提交数据
         * */
        if (!req.isExpectMultipart) {
            throw IllegalStateException("HttpServerRequest should have set expect Multipart")
        }
        val params = req.formAttributes()
        val username = params.get("username")
        val password = params.get("password")

        /**
         * 校验用户名与密码是否存在
         * */
        if (username == null || password == null) {
            context.fail(400, NullPointerException("No username or password provided in form?"))
            return
        }
        val session = context.session()
        val authInfo = JsonObject().put("username", username).put("password", password)
        authProvider.authenticate(authInfo){ ar ->
            if (ar.succeeded()) {
                val user = ar.result()
                context.setUser(user)
                session?.regenerateId()
                req.response().success(user.principal())
            } else {
                /*返回错误状态*/
                context.fail(ar.cause())
            }
        }
    }

}