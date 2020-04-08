package kavi.tech.service.ms

import io.vertx.codegen.annotations.GenIgnore
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.serviceproxy.ServiceProxyBuilder




/**
 * ProxyGen 声明要处理的Java接口类型，以生成可通过Vert.x事件总线连接到原始API的Java代理。
 * VertxGen 声明要处理的Java接口类型，以便用polyglot Vert.x的各种语言创建API。
 * */
@ProxyGen
@VertxGen
interface UserService {

    fun list(document: JsonObject, handler: Handler<AsyncResult<JsonArray>>)

    /**
     * 注册信息
     * */
    @GenIgnore
    object Factory{
        const val name = "UserService"
        const val address = "kavi.tech.service.ms.user"

        @JvmStatic
        fun create(vertx: Vertx): UserService {
            return ServiceProxyBuilder(vertx).setAddress(address).build(UserService::class.java)
        }
    }

}