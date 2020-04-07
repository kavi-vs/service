package kavi.tech.service.web.admin

import io.vertx.core.Vertx
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import kavi.tech.service.common.auth.MybatisAuthHandler
import kavi.tech.service.common.auth.MybatisAuthProvider
import org.springframework.beans.factory.annotation.Autowired
import tech.kavi.vs.web.Controller
import tech.kavi.vs.web.HandlerScan

/**
 * 管理平台入口控制器
 * */
@HandlerScan
class AdminController @Autowired constructor(
    override val vertx: Vertx,
    private val authHandler: MybatisAuthHandler,
    private val authProvider: MybatisAuthProvider
) : Controller({

    /*接收表单数据*/
    route().handler(BodyHandler.create())

    /*会话处理器*/
    route().handler(SessionHandler.create(LocalSessionStore.create(vertx)).setAuthProvider(authProvider))

    /*账户登入*/
    post("/login").handler(authHandler::login)

    /*限制PUT/POST访问权限*/
    // post("/*").handler(authHandler)
})
