package kavi.tech.service.common.extension

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.web.handler.impl.HttpStatusException

/**
 * 返回用户信息实例
 * */
inline fun <reified T> User.principal(): T? {
    return try {
        this.principal().mapTo(T::class.java)
    } catch (e: Exception) {
        null
    }
}


/**
 * 返回JSON的成功信息
 * */
fun HttpServerResponse.success(data: Any, json: JsonObject ?= null) {
    val result = JsonObject().put("data", data)
    if (json != null) result.mergeIn(json)
    this.putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(result))
}

/**
 * 返回JSON格式的错误码信息
 * @param e 异常类
 * @param json 附加JsonObject信息
 * */
fun HttpServerResponse.error(e: Throwable, json: JsonObject ?= null) {
    val result = JsonObject()
    val cause = e.cause
    if (e is HttpStatusException && cause != null) {
        result.put("error", cause.javaClass.simpleName).put("message", cause.message)
    } else {
        result.put("error", e.javaClass.simpleName).put("message", e.message)
    }

    if (json != null) result.mergeIn(json)
    this.putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(result))
}